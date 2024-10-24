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
        List<LinearizationDefinition> definitionList = linearizationService.getLinearizationDefinitions();
        List<TableConfiguration> configurations = configRepository.getALlTableConfiguration();
        readWriteLock.executeWriteLock(() -> {
            stream.collect(StreamUtils.batchCollector(500, createBatchProcessorForSavingPaginatedHistories(projectId, userId, definitionList, configurations)));
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
                    PostCoordinationCustomScalesRevision revision = new PostCoordinationCustomScalesRevision(userId, new Date().getTime(), events);
                    EntityCustomScalesValuesHistory history = new EntityCustomScalesValuesHistory(specification.whoficEntityIri(), projectId.id(), List.of(revision));
                    histories.add(history);
                }
                var documents = histories.stream()
                        .map(history -> new InsertOneModel<>(objectMapper.convertValue(history, Document.class)))
                        .toList();

                repository.bulkWriteDocuments(documents, POSTCOORDINATION_CUSTOM_SCALES_COLLECTION);

                newRevisionsEventEmitter.emitNewRevisionsEventForScaleHistory(projectId, new ArrayList<>(histories));
            }
        };
    }


    private Consumer<List<WhoficEntityPostCoordinationSpecification>> createBatchProcessorForSavingPaginatedHistories(ProjectId projectId, UserId userId, List<LinearizationDefinition> definitionList, List<TableConfiguration> configurations) {
        return page -> {
            if (isNotEmpty(page)) {
                Set<EntityPostCoordinationHistory> histories = new HashSet<>();
                for (WhoficEntityPostCoordinationSpecification specification : page) {

                    for (LinearizationDefinition linearizationDefinition : definitionList) {
                        boolean linearizationExists = specification.postcoordinationSpecifications().stream().anyMatch(spec ->
                                spec.getLinearizationView().equalsIgnoreCase(linearizationDefinition.getWhoficEntityIri()));
                        if (!linearizationExists) {
                            specification.postcoordinationSpecifications().add(new PostCoordinationSpecification(linearizationDefinition.getWhoficEntityIri(),
                                    new ArrayList<>(),
                                    new ArrayList<>(),
                                    new ArrayList<>(),
                                    new ArrayList<>()));
                        }

                    }

                    Set<PostCoordinationViewEvent> events = specification.postcoordinationSpecifications().stream()
                            .map(spec -> enrichWithMissingAxis(specification.entityType(), spec, definitionList, configurations))
                            .map(spec ->
                                    new PostCoordinationViewEvent(spec.getLinearizationView(), SpecificationToEventsMapper.convertFromSpecification(spec))
                            )
                            .collect(Collectors.toSet());
                    PostCoordinationSpecificationRevision revision = new PostCoordinationSpecificationRevision(userId, new Date().getTime(), events);
                    EntityPostCoordinationHistory history = new EntityPostCoordinationHistory(specification.whoficEntityIri(), projectId.id(), List.of(revision));
                    histories.add(history);
                }

                saveMultipleEntityPostCoordinationHistories(histories);

                newRevisionsEventEmitter.emitNewRevisionsEventForSpecHistory(projectId, histories.stream().toList());
            }
        };
    }

    private void saveMultipleEntityPostCoordinationHistories(Set<EntityPostCoordinationHistory> historiesToBeSaved) {
        var documents = historiesToBeSaved.stream()
                .map(history -> new InsertOneModel<>(objectMapper.convertValue(history, Document.class)))
                .toList();

        repository.bulkWriteDocuments(documents, POSTCOORDINATION_HISTORY_COLLECTION);
    }


    PostCoordinationSpecification enrichWithMissingAxis(String entityType, PostCoordinationSpecification specification, List<LinearizationDefinition> definitionList, List<TableConfiguration> configurations) {
        LinearizationDefinition definition = definitionList.stream()
                .filter(linearizationDefinition -> linearizationDefinition.getWhoficEntityIri().equalsIgnoreCase(specification.getLinearizationView()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the linearization definition " + specification.getLinearizationView()));

        TableConfiguration tableConfiguration = configurations.stream()
                .filter(config -> config.getEntityType().equalsIgnoreCase(entityType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the equivalent entity type " + entityType));

        for (String availableAxis : tableConfiguration.getPostCoordinationAxes()) {
            boolean isAlreadySet = specification.getRequiredAxes().contains(availableAxis) ||
                    specification.getAllowedAxes().contains(availableAxis) ||
                    specification.getNotAllowedAxes().contains(availableAxis) ||
                    specification.getDefaultAxes().contains(availableAxis);
            if (!isAlreadySet) {
                if (definition.getCoreLinId() != null && !definition.getCoreLinId().isEmpty()) {
                    specification.getDefaultAxes().add(availableAxis);
                } else {
                    specification.getNotAllowedAxes().add(availableAxis);
                }
            }
        }
        return specification;
    }

    public void addSpecificationRevision(WhoficEntityPostCoordinationSpecification newSpecification, UserId userId, ProjectId projectId) {

        readWriteLock.executeWriteLock(() -> {
                    var existingHistoryOptional = this.repository.getExistingHistoryOrderedByRevision(newSpecification.whoficEntityIri(), projectId);
                    existingHistoryOptional.ifPresentOrElse(history -> {

                                WhoficEntityPostCoordinationSpecification oldSpec = eventProcessor.processHistory(history);

                                Set<PostCoordinationViewEvent> specEvents = SpecificationToEventsMapper.createEventsFromDiff(oldSpec, newSpecification);


                                if (!specEvents.isEmpty()) {
                                    var newRevision = new PostCoordinationSpecificationRevision(userId, new Date().getTime(), specEvents);
                                    repository.addSpecificationRevision(newSpecification.whoficEntityIri(), projectId, newRevision);
                                    newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, newSpecification.whoficEntityIri(), newRevision);
                                }
                            }, () -> {
                                WhoficEntityPostCoordinationSpecification oldSpec = WhoficEntityPostCoordinationSpecification.create(newSpecification.whoficEntityIri(), newSpecification.entityType(), Collections.emptyList());
                                Set<PostCoordinationViewEvent> specEvents = SpecificationToEventsMapper.createEventsFromDiff(oldSpec, newSpecification);

                                if (!specEvents.isEmpty()) {
                                    var newRevision = new PostCoordinationSpecificationRevision(userId, new Date().getTime(), specEvents);
                                    repository.addSpecificationRevision(newSpecification.whoficEntityIri(), projectId, newRevision);
                                    newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, newSpecification.whoficEntityIri(), newRevision);
                                }
                            }
                    );
                }
        );
    }

    public void addCustomScaleRevision(WhoficCustomScalesValues newScales,
                                       ProjectId projectId, UserId userId) {
        readWriteLock.executeWriteLock(() -> {
                    var existingScaleHistoryOptional = this.repository.getExistingCustomScaleHistoryOrderedByRevision(newScales.whoficEntityIri(), projectId);
                    existingScaleHistoryOptional.ifPresentOrElse(history -> {

                                WhoficCustomScalesValues oldSpec = eventProcessor.processCustomScaleHistory(history);

                                Set<PostCoordinationCustomScalesValueEvent> events = SpecificationToEventsMapper.createScaleEventsFromDiff(oldSpec, newScales);


                                if (!events.isEmpty()) {
                                    var newRevision = new PostCoordinationCustomScalesRevision(userId, new Date().getTime(), events);
                                    repository.addCustomScalesRevision(newScales.whoficEntityIri(), projectId, newRevision);
                                    newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, newScales.whoficEntityIri(), newRevision);
                                }
                            }, () -> {
                                WhoficCustomScalesValues oldSpec = WhoficCustomScalesValues.create(newScales.whoficEntityIri(), Collections.emptyList());
                                Set<PostCoordinationCustomScalesValueEvent> events = SpecificationToEventsMapper.createScaleEventsFromDiff(oldSpec, newScales);

                                if (!events.isEmpty()) {
                                    var newRevision = new PostCoordinationCustomScalesRevision(userId, new Date().getTime(), events);
                                    repository.addCustomScalesRevision(newScales.whoficEntityIri(), projectId, newRevision);
                                    newRevisionsEventEmitter.emitNewRevisionsEvent(projectId, newScales.whoficEntityIri(), newRevision);
                                }
                            }
                    );
                }
        );
    }

    public WhoficCustomScalesValues fetchCustomScalesHistory(String entityIri, ProjectId projectId) {
        return this.repository.getExistingCustomScaleHistoryOrderedByRevision(entityIri, projectId)
                .map(eventProcessor::processCustomScaleHistory)
                .orElseGet(() -> new WhoficCustomScalesValues(entityIri, Collections.emptyList()));

    }

    public WhoficEntityPostCoordinationSpecification fetchHistory(String entityIri, ProjectId projectId) {
        return this.repository.getExistingHistoryOrderedByRevision(entityIri, projectId)
                .map(eventProcessor::processHistory)
                .orElseGet(() -> new WhoficEntityPostCoordinationSpecification(entityIri, null, Collections.emptyList()));
    }

}
