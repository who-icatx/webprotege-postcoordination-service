package edu.stanford.protege.webprotege.postcoordinationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.InsertOneModel;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotege.postcoordinationservice.StreamUtils;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.mappers.SpecificationToEventsMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationRevision;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecificationRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableConfiguration;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationDocumentRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationSpecificationsRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationTableConfigRepository;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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


    public void createFirstImport(String documentLocation, ProjectId projectId, UserId userId) {
        var stream = documentRepository.fetchFromDocument(documentLocation);
        List<LinearizationDefinition> definitionList = linearizationService.getLinearizationDefinitions();
        List<TableConfiguration> configurations = configRepository.getALlTableConfiguration();
        readWriteLock.executeWriteLock(() -> {
            stream.collect(StreamUtils.batchCollector(500, createBatchProcessorForSavingPaginatedHistories(projectId, userId, definitionList, configurations)));
        });
    }


    public Consumer<List<WhoficEntityPostCoordinationSpecification>> createBatchProcessorForSavingPaginatedHistories(ProjectId projectId, UserId userId , List<LinearizationDefinition> definitionList, List<TableConfiguration> configurations) {
        return page -> {
            if (isNotEmpty(page)) {
                Set<EntityPostCoordinationHistory> histories = new HashSet<>();
                for(WhoficEntityPostCoordinationSpecification specification: page) {
                    Set<PostCoordinationEvent> events = specification.getPostCoordinationSpecifications().stream()
                            .map(spec -> enrichWithMissingAxis(specification.getEntityType(), spec, definitionList, configurations))
                            .flatMap(spec -> SpecificationToEventsMapper.convertFromSpecification(spec).stream())
                            .collect(Collectors.toSet());
                    PostCoordinationRevision revision = new PostCoordinationRevision(userId.id(), new Date().getTime(), events);
                    EntityPostCoordinationHistory history = new EntityPostCoordinationHistory(specification.getWhoficEntityIri(), projectId.id(), new HashSet<>(List.of(revision)));
                    histories.add(history);
                }

                saveMultipleEntityPostCoordinationHistories(histories);
            }
        };
    }

    public void saveMultipleEntityPostCoordinationHistories(Set<EntityPostCoordinationHistory> historiesToBeSaved) {
        var documents = historiesToBeSaved.stream()
                .map(history -> new InsertOneModel<>(objectMapper.convertValue(history, Document.class)))
                .toList();

        specRepository.bulkWriteDocuments(documents);
    }


    public PostCoordinationSpecificationRequest enrichWithMissingAxis(String entityType, PostCoordinationSpecificationRequest specification, List<LinearizationDefinition> definitionList, List<TableConfiguration> configurations) {
        LinearizationDefinition definition = definitionList.stream()
                .filter(linearizationDefinition -> linearizationDefinition.getWhoficEntityIri().equalsIgnoreCase(specification.getLinearizationView()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the linearization definition " + specification.getLinearizationView()));

        TableConfiguration tableConfiguration = configurations.stream()
                .filter(config -> config.getEntityType().equalsIgnoreCase(entityType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the equivalent entity type " + entityType));

        for(String availableAxis : tableConfiguration.getPostCoordinationAxes()) {
            boolean isAlreadySet = specification.getRequiredAxes().contains(availableAxis) ||
                                   specification.getAllowedAxes().contains(availableAxis) ||
                                   specification.getNotAllowedAxes().contains(availableAxis) ||
                                   specification.getDefaultAxes().contains(availableAxis);
            if(!isAlreadySet) {
                if(definition.getCoreLinId() != null && !definition.getCoreLinId().isEmpty()) {
                    specification.getDefaultAxes().add(availableAxis);
                } else {
                    specification.getNotAllowedAxes().add(availableAxis);
                }
            }
        }
        return specification;
    }

}
