package edu.stanford.protege.webprotege.postcoordinationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.InsertOneModel;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotege.postcoordinationservice.StreamUtils;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.mappers.SpecificationToEventsMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationDocumentRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationSpecificationsRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationTableConfigRepository;
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


    private final PostCoordinationSpecificationsRepository specRepository;
    private final PostCoordinationTableConfigRepository configRepository;

    private final LinearizationService linearizationService;
    private final ReadWriteLockService readWriteLock;

    private final PostCoordinationDocumentRepository documentRepository;

    private final ObjectMapper objectMapper;

    public PostCoordinationService(PostCoordinationSpecificationsRepository specRepository,
                                   PostCoordinationTableConfigRepository configRepository,
                                   LinearizationService linearizationService,
                                   ReadWriteLockService readWriteLock,
                                   PostCoordinationDocumentRepository documentRepository,
                                   ObjectMapper objectMapper) {
        this.specRepository = specRepository;
        this.configRepository = configRepository;
        this.linearizationService = linearizationService;
        this.readWriteLock = readWriteLock;
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
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

    public Optional<EntityPostCoordinationHistory> getExistingHistoryOrderedByRevision(String entityIri, ProjectId projectId) {
        return specRepository.findHistoryByEntityIriAndProjectId(entityIri, projectId)
                .map(history -> {
                    List<PostCoordinationRevision> sortedRevisions = history.getPostCoordinationRevisions()
                            .stream()
                            .sorted(Comparator.comparingLong(PostCoordinationRevision::timestamp))
                            .collect(Collectors.toList());
                    // Return a new EntityLinearizationHistory object with the sorted revisions
                    return new EntityPostCoordinationHistory(history.getWhoficEntityIri(), history.getProjectId(), sortedRevisions);
                });

    }

    private Consumer<List<WhoficCustomScalesValues>> createBatchProcessorForSavingPaginatedCustomScales(ProjectId projectId,
                                                                                                                         UserId userId) {
        return page -> {
            if (isNotEmpty(page)) {
                Set<EntityCustomScalesValuesHistory> histories = new HashSet<>();
                for (WhoficCustomScalesValues specification : page) {
                    Set<PostCoordinationCustomScalesValueEvent> events = SpecificationToEventsMapper.convertToFirstImportEvents(specification);
                    PostCoordinationCustomScalesRevision revision = new PostCoordinationCustomScalesRevision(userId.id(), new Date().getTime(), events);
                    EntityCustomScalesValuesHistory history = new EntityCustomScalesValuesHistory(specification.whoficEntityIri(), projectId.id(), List.of(revision));
                    histories.add(history);
                }
                var documents = histories.stream()
                        .map(history -> new InsertOneModel<>(objectMapper.convertValue(history, Document.class)))
                        .toList();

                specRepository.bulkWriteDocuments(documents, POSTCOORDINATION_CUSTOM_SCALES_COLLECTION);

            }
        };
    }


    private Consumer<List<WhoficEntityPostCoordinationSpecification>> createBatchProcessorForSavingPaginatedHistories(ProjectId projectId, UserId userId, List<LinearizationDefinition> definitionList, List<TableConfiguration> configurations) {
        return page -> {
            if (isNotEmpty(page)) {
                Set<EntityPostCoordinationHistory> histories = new HashSet<>();
                for (WhoficEntityPostCoordinationSpecification specification : page) {
                    Set<PostCoordinationViewEvent> events = specification.postCoordinationSpecifications().stream()
                            .map(spec -> enrichWithMissingAxis(specification.entityType(), spec, definitionList, configurations))
                            .map(spec ->
                                    new PostCoordinationViewEvent(spec.getLinearizationView(), SpecificationToEventsMapper.convertFromSpecification(spec))
                            )
                            .collect(Collectors.toSet());
                    PostCoordinationRevision revision = new PostCoordinationRevision(userId.id(), new Date().getTime(), events);
                    EntityPostCoordinationHistory history = new EntityPostCoordinationHistory(specification.whoficEntityIri(), projectId.id(), List.of(revision));
                    histories.add(history);
                }

                saveMultipleEntityPostCoordinationHistories(histories);
            }
        };
    }

    private void saveMultipleEntityPostCoordinationHistories(Set<EntityPostCoordinationHistory> historiesToBeSaved) {
        var documents = historiesToBeSaved.stream()
                .map(history -> new InsertOneModel<>(objectMapper.convertValue(history, Document.class)))
                .toList();

        specRepository.bulkWriteDocuments(documents, POSTCOORDINATION_HISTORY_COLLECTION);
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

}
