package edu.stanford.protege.webprotege.postcoordinationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.InsertOneModel;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.postcoordinationservice.StreamUtils;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.mappers.SpecificationToEventsMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.*;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.POSTCOORDINATION_CUSTOM_SCALES_COLLECTION;
import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.POSTCOORDINATION_HISTORY_COLLECTION;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
public class PostCoordinationService {


    private final PostCoordinationRepository repository;
    private final PostCoordinationTableConfigRepository configRepository;
    private final LinearizationService linearizationService;
    private final ReadWriteLockService readWriteLock;
    private final PostCoordinationDocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final NewRevisionsEventEmitterService newRevisionsEventEmitter;

    private final PostCoordinationEventProcessor eventProcessor;


    public PostCoordinationService(PostCoordinationRepository repository,
                                   PostCoordinationTableConfigRepository configRepository,
                                   LinearizationService linearizationService,
                                   ReadWriteLockService readWriteLock,
                                   PostCoordinationDocumentRepository documentRepository,
                                   ObjectMapper objectMapper,
                                   NewRevisionsEventEmitterService newRevisionsEventEmitter, PostCoordinationEventProcessor eventProcessor) {
        this.repository = repository;
        this.configRepository = configRepository;
        this.linearizationService = linearizationService;
        this.readWriteLock = readWriteLock;
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
        this.newRevisionsEventEmitter = newRevisionsEventEmitter;
        this.eventProcessor = eventProcessor;
    }


    public void createFirstSpecificationImport(String documentLocation, ProjectId projectId, UserId userId) {
        var stream = documentRepository.fetchPostCoordinationSpecifications(documentLocation);
        readWriteLock.executeWriteLock(() -> {
            stream.collect(StreamUtils.batchCollector(500, createBatchProcessorForSavingPaginatedHistories(projectId, userId)));
        });
    }


    public void crateFirstCustomScalesValuesImport(String documentLocation, ProjectId projectId, UserId userId) {
        var stream = documentRepository.fetchCustomScalesValues(documentLocation);
        readWriteLock.executeWriteLock(() -> {
            stream.collect(StreamUtils.batchCollector(500, createBatchProcessorForSavingPaginatedCustomScales(projectId, userId)));
        });
    }

    private Consumer<List<WhoficCustomScalesValues>> createBatchProcessorForSavingPaginatedCustomScales(ProjectId projectId,
                                                                                                        UserId userId) {
        return page -> {
            if (isNotEmpty(page)) {
                Set<EntityCustomScalesValuesHistory> histories = new HashSet<>();
                for (WhoficCustomScalesValues specification : page) {
                    Set<PostCoordinationCustomScalesValueEvent> events = SpecificationToEventsMapper.convertToFirstImportEvents(specification);
                    PostCoordinationCustomScalesRevision revision = PostCoordinationCustomScalesRevision.create(userId, events);
                    EntityCustomScalesValuesHistory history = new EntityCustomScalesValuesHistory(specification.whoficEntityIri(), projectId.id(), List.of(revision));
                    histories.add(history);
                }
                var documents = histories.stream()
                        .map(history -> new InsertOneModel<>(objectMapper.convertValue(history, Document.class)))
                        .toList();

                repository.bulkWriteDocuments(documents, POSTCOORDINATION_CUSTOM_SCALES_COLLECTION);

                newRevisionsEventEmitter.emitNewRevisionsEventForScaleHistory(projectId, new ArrayList<>(histories), null);
            }
        };
    }


    private Consumer<List<WhoficEntityPostCoordinationSpecification>> createBatchProcessorForSavingPaginatedHistories(ProjectId projectId, UserId userId) {
        return page -> {
            if (isNotEmpty(page)) {
                Set<EntityPostCoordinationHistory> histories = new HashSet<>();
                for (WhoficEntityPostCoordinationSpecification specification : page) {
                    Set<PostCoordinationViewEvent> events = specification.postcoordinationSpecifications().stream()
                            .map(spec ->
                                    new PostCoordinationViewEvent(spec.getLinearizationView(), SpecificationToEventsMapper.convertFromSpecification(spec))
                            )
                            .filter(spec -> !spec.axisEvents().isEmpty())
                            .collect(Collectors.toSet());
                    PostCoordinationSpecificationRevision revision = PostCoordinationSpecificationRevision.create(userId, events);
                    EntityPostCoordinationHistory history = new EntityPostCoordinationHistory(specification.whoficEntityIri(),  projectId.id(), List.of(revision));
                    if(!events.isEmpty()) {
                        histories.add(history);
                    }
                }
                if(!histories.isEmpty()) {
                    saveMultipleEntityPostCoordinationHistories(histories);
                }

            }
        };
    }

    private void saveMultipleEntityPostCoordinationHistories(Set<EntityPostCoordinationHistory> historiesToBeSaved) {
        var documents = historiesToBeSaved.stream()
                .map(history -> new InsertOneModel<>(objectMapper.convertValue(history, Document.class)))
                .toList();

        repository.bulkWriteDocuments(documents, POSTCOORDINATION_HISTORY_COLLECTION);
    }


    public void addSpecificationRevision(WhoficEntityPostCoordinationSpecification newSpecification, UserId userId, ProjectId projectId) {
        addSpecificationRevision(newSpecification, userId, projectId, null);
    }

    public void addSpecificationRevision(WhoficEntityPostCoordinationSpecification newSpecification, UserId userId, ProjectId projectId, ChangeRequestId changeRequestId) {

        readWriteLock.executeWriteLock(() -> {
                    var existingHistoryOptional = this.repository.getExistingHistoryOrderedByRevision(newSpecification.whoficEntityIri(), projectId);
                    existingHistoryOptional.ifPresentOrElse(history -> {

                                WhoficEntityPostCoordinationSpecification oldSpec = eventProcessor.processHistory(history);
                                Set<PostCoordinationViewEvent> specEvents = SpecificationToEventsMapper.createEventsFromDiff(oldSpec, newSpecification);

                                if (!specEvents.isEmpty()) {
                                    var newRevision = PostCoordinationSpecificationRevision.create(userId, specEvents, changeRequestId);
                                    repository.addSpecificationRevision(newSpecification.whoficEntityIri(), projectId, newRevision);
                                    newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, newSpecification.whoficEntityIri(), newRevision, changeRequestId);
                                }
                            }, () -> {
                                EntityPostCoordinationHistory history = createNewSpecificationHistory(newSpecification, projectId, userId, changeRequestId);
                                var savedHistory = repository.saveNewSpecificationHistory(history);
                                savedHistory.getPostCoordinationRevisions()
                                        .stream()
                                        .findFirst()
                                        .ifPresent(revision -> newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, savedHistory.getWhoficEntityIri(), revision, changeRequestId));
                            }
                    );
                }
        );
    }

    public void addCustomScaleRevision(WhoficCustomScalesValues newScales,
                                       ProjectId projectId,
                                       UserId userId) {
        addCustomScaleRevision(newScales, projectId, userId, null);
    }
    public void addCustomScaleRevision(WhoficCustomScalesValues newScales,
                                       ProjectId projectId,
                                       UserId userId,
                                       ChangeRequestId changeRequestId) {
        readWriteLock.executeWriteLock(() -> {
                    var existingScaleHistoryOptional = this.repository.getExistingCustomScaleHistoryOrderedByRevision(newScales.whoficEntityIri(), projectId);
                    existingScaleHistoryOptional.ifPresentOrElse(history -> {

                                WhoficCustomScalesValues oldSpec = eventProcessor.processCustomScaleHistory(history);

                                Set<PostCoordinationCustomScalesValueEvent> events = SpecificationToEventsMapper.createScaleEventsFromDiff(oldSpec, newScales);


                                if (!events.isEmpty()) {
                                    var newRevision = PostCoordinationCustomScalesRevision.create(userId, events, changeRequestId);
                                    repository.addCustomScalesRevision(newScales.whoficEntityIri(), projectId, newRevision);
                                    newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, newScales.whoficEntityIri(), newRevision, changeRequestId);
                                }
                            }, () -> {
                                var newHistory = createNewEntityCustomScalesHistory(newScales, projectId, userId, changeRequestId);
                                var savedHistory = repository.saveNewCustomScalesHistory(newHistory);
                                savedHistory.getPostCoordinationCustomScalesRevisions()
                                        .stream()
                                        .findFirst()
                                        .ifPresent(revision -> newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, newScales.whoficEntityIri(), revision, changeRequestId));
                            }
                    );
                }
        );
    }

    private EntityCustomScalesValuesHistory createNewEntityCustomScalesHistory(WhoficCustomScalesValues newScales,
                                                                               ProjectId projectId,
                                                                               UserId userId,
                                                                               ChangeRequestId changeRequestId) {
        WhoficCustomScalesValues oldSpec = WhoficCustomScalesValues.create(newScales.whoficEntityIri(), Collections.emptyList());
        Set<PostCoordinationCustomScalesValueEvent> events = SpecificationToEventsMapper.createScaleEventsFromDiff(oldSpec, newScales);
        var revision = PostCoordinationCustomScalesRevision.create(userId, events, changeRequestId);
        return EntityCustomScalesValuesHistory.create(newScales.whoficEntityIri(), projectId.value(), List.of(revision));
    }

    private EntityPostCoordinationHistory createNewSpecificationHistory(WhoficEntityPostCoordinationSpecification newSpec,
                                                                        ProjectId projectId,
                                                                        UserId userId,
                                                                        ChangeRequestId changeRequestId) {
        List<LinearizationDefinition> definitionList = linearizationService.getLinearizationDefinitions();
        List<TableConfiguration> configurations = configRepository.getALlTableConfiguration();
        var defaultRevision = PostCoordinationSpecificationRevision.createDefaultInitialRevision(newSpec.entityType(),
                definitionList,
                configurations);

        WhoficEntityPostCoordinationSpecification defaultSpec = eventProcessor.processHistory(new EntityPostCoordinationHistory(newSpec.whoficEntityIri(), projectId.id(), Arrays.asList(defaultRevision)));
        Set<PostCoordinationViewEvent> specEvents = SpecificationToEventsMapper.createEventsFromDiff(defaultSpec, newSpec);

        var newRevision = PostCoordinationSpecificationRevision.create(userId, specEvents, changeRequestId);
        return EntityPostCoordinationHistory.create(newSpec.whoficEntityIri(), projectId.id(), List.of(newRevision));
    }

    public GetEntityCustomScaleValueResponse fetchCustomScalesHistory(String entityIri, ProjectId projectId) {
        return this.repository.getExistingCustomScaleHistoryOrderedByRevision(entityIri, projectId)
                .map(history -> {
                    Date lastRevisionDate = null;
                    if (!history.getPostCoordinationCustomScalesRevisions().isEmpty()) {
                        long lastRevisionTimestamp = history.getPostCoordinationCustomScalesRevisions().get(history.getPostCoordinationCustomScalesRevisions().size() - 1).timestamp();
                        lastRevisionDate = Date.from(Instant.ofEpochMilli(lastRevisionTimestamp));
                    }
                    WhoficCustomScalesValues scales = eventProcessor.processCustomScaleHistory(history);
                    return new GetEntityCustomScaleValueResponse(lastRevisionDate, scales);
                })
                .orElseGet(() -> new GetEntityCustomScaleValueResponse(null, new WhoficCustomScalesValues(entityIri, Collections.emptyList())));

    }

    public GetEntityPostCoordinationResponse fetchHistory(String entityIri, ProjectId projectId, String entityType) {
        List<LinearizationDefinition> definitionList = linearizationService.getLinearizationDefinitions();
        List<TableConfiguration> configurations = configRepository.getALlTableConfiguration();
        return this.repository.getExistingHistoryOrderedByRevision(entityIri, projectId)
                .map(history -> {
                            history.getPostCoordinationRevisions().add(0, PostCoordinationSpecificationRevision.createDefaultInitialRevision(
                                    entityType,
                                    definitionList,
                                    configurations));
                            return new GetEntityPostCoordinationResponse(entityIri, eventProcessor.processHistory(history));
                        }
                )
                .orElseGet(() -> {
                    var specs =  Arrays.asList(PostCoordinationSpecificationRevision.createDefaultInitialRevision(entityType, definitionList, configurations));
                    var history = new EntityPostCoordinationHistory(entityIri, projectId.id(), specs);
                    return new GetEntityPostCoordinationResponse(entityIri, eventProcessor.processHistory(history));
                });
    }

}
