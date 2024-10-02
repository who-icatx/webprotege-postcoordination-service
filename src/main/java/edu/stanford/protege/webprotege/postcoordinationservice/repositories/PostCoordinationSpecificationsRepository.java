package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.result.UpdateResult;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.ReadWriteLockService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.POSTCOORDINATION_CUSTOM_SCALES_COLLECTION;
import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.POSTCOORDINATION_HISTORY_COLLECTION;

@Repository
public class PostCoordinationSpecificationsRepository {

    public static final String WHOFIC_ENTITY_IRI = "whoficEntityIri";
    public static final String PROJECT_ID = "projectId";
    public static final String POSTCOORDINATION_SPECIFICATION_REVISIONS = "postCoordinationRevisions";
    public static final String POSTCOORDINATION_CUSTOM_SCALES_REVISIONS = "postCoordinationCustomScalesRevisions";


    private final MongoTemplate mongoTemplate;
    private final ReadWriteLockService readWriteLock;


    public PostCoordinationSpecificationsRepository(MongoTemplate mongoTemplate, ReadWriteLockService readWriteLock) {
        this.mongoTemplate = mongoTemplate;
        this.readWriteLock = readWriteLock;
    }

    public void bulkWriteDocuments(List<InsertOneModel<Document>> listOfInsertOneModelDocument, String collectionName) {
        readWriteLock.executeWriteLock(() -> {
            var collection = mongoTemplate.getCollection(collectionName);
            collection.bulkWrite(listOfInsertOneModelDocument);
        });
    }

    public void addSpecificationRevision(String whoficEntityIri, ProjectId projectId, PostCoordinationSpecificationRevision specificationRevision) {
        Query query = new Query();
        query.addCriteria(Criteria.where(WHOFIC_ENTITY_IRI).is(whoficEntityIri)
                .and(PROJECT_ID).is(projectId.id()));

        Update update = new Update();
        update.push(POSTCOORDINATION_SPECIFICATION_REVISIONS, specificationRevision);

        readWriteLock.executeWriteLock(() -> {
            UpdateResult result = mongoTemplate.updateFirst(query, update, EntityPostCoordinationHistory.class, POSTCOORDINATION_HISTORY_COLLECTION);
            if (result.getMatchedCount() == 0) {
                EntityPostCoordinationHistory history = new EntityPostCoordinationHistory(whoficEntityIri,projectId.id(), Arrays.asList(specificationRevision));
                mongoTemplate.save(history);
            }
        });
    }

    public void addCustomScalesRevision(String whoficEntityIri, ProjectId projectId, PostCoordinationCustomScalesRevision customScalesRevision) {
        Query query = new Query();
        query.addCriteria(Criteria.where(WHOFIC_ENTITY_IRI).is(whoficEntityIri)
                .and(PROJECT_ID).is(projectId.id()));

        Update update = new Update();
        update.push(POSTCOORDINATION_CUSTOM_SCALES_REVISIONS, customScalesRevision);

        readWriteLock.executeWriteLock(() -> {
            UpdateResult result = mongoTemplate.updateFirst(query, update, EntityCustomScalesValuesHistory.class, POSTCOORDINATION_CUSTOM_SCALES_COLLECTION);
            if (result.getMatchedCount() == 0) {
                throw new IllegalArgumentException(POSTCOORDINATION_CUSTOM_SCALES_COLLECTION + " not found for the given " +
                        WHOFIC_ENTITY_IRI + ":" + whoficEntityIri + " and " + PROJECT_ID +
                        ":" + projectId + ".");
            }
        });
    }


    public void writeDocument(Document document, String collectionName) {
        readWriteLock.executeWriteLock(() -> {
            var collection = mongoTemplate.getCollection(collectionName);
            collection.insertOne(document);
        });
    }
    public Optional<EntityPostCoordinationHistory> findHistoryByEntityIriAndProjectId(String entityIri, ProjectId projectId) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where(WHOFIC_ENTITY_IRI).is(entityIri)
                        .and(PROJECT_ID).is(projectId.value())
        );

        return readWriteLock.executeReadLock(() -> Optional.ofNullable(mongoTemplate.findOne(query, EntityPostCoordinationHistory.class, POSTCOORDINATION_HISTORY_COLLECTION)));
    }
    public Optional<EntityPostCoordinationHistory> getExistingHistoryOrderedByRevision(String entityIri, ProjectId projectId) {
        return findHistoryByEntityIriAndProjectId(entityIri, projectId)
                .map(history -> {
                    List<PostCoordinationSpecificationRevision> sortedRevisions = history.getPostCoordinationRevisions()
                            .stream()
                            .sorted(Comparator.comparingLong(PostCoordinationSpecificationRevision::timestamp))
                            .collect(Collectors.toList());
                    // Return a new EntityLinearizationHistory object with the sorted revisions
                    return new EntityPostCoordinationHistory(history.getWhoficEntityIri(), history.getProjectId(), sortedRevisions);
                });
    }

    public Optional<EntityCustomScalesValuesHistory> getExistingCustomScaleHistoryOrderedByRevision(String entityIri, ProjectId projectId) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where(WHOFIC_ENTITY_IRI).is(entityIri)
                        .and(PROJECT_ID).is(projectId.value())
        );

       return readWriteLock.executeReadLock(() ->
               Optional.ofNullable(mongoTemplate.findOne(query, EntityCustomScalesValuesHistory.class, POSTCOORDINATION_CUSTOM_SCALES_COLLECTION))
       ).map(history -> {
           List<PostCoordinationCustomScalesRevision> sortedRevisions = history.getPostCoordinationCustomScalesRevisions()
                   .stream()
                   .sorted(Comparator.comparingLong(PostCoordinationCustomScalesRevision::timestamp))
                   .collect(Collectors.toList());
           return new EntityCustomScalesValuesHistory(history.getWhoficEntityIri(), history.getProjectId(), sortedRevisions);
       });

    }
}
