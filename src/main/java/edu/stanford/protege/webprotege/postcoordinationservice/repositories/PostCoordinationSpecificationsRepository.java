package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import com.mongodb.client.model.InsertOneModel;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory;
import edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationCustomScalesRevision;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationRevision;
import edu.stanford.protege.webprotege.postcoordinationservice.services.ReadWriteLockService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.POSTCOORDINATION_HISTORY_COLLECTION;

@Repository
public class PostCoordinationSpecificationsRepository {

    public static final String WHOFIC_ENTITY_IRI = "whoficEntityIri";
    public static final String PROJECT_ID = "projectId";


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


    public void writeDocument(Document document) {
        readWriteLock.executeWriteLock(() -> {
            var collection = mongoTemplate.getCollection(POSTCOORDINATION_HISTORY_COLLECTION);
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
                    List<PostCoordinationRevision> sortedRevisions = history.getPostCoordinationRevisions()
                            .stream()
                            .sorted(Comparator.comparingLong(PostCoordinationRevision::timestamp))
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
               Optional.ofNullable(mongoTemplate.findOne(query, EntityCustomScalesValuesHistory.class, EntityCustomScalesValuesHistory.POSTCOORDINATION_CUSTOM_SCALES_COLLECTION))
       ).map(history -> {
           List<PostCoordinationCustomScalesRevision> sortedRevisions = history.getPostCoordinationCustomScalesRevisions()
                   .stream()
                   .sorted(Comparator.comparingLong(PostCoordinationCustomScalesRevision::timestamp))
                   .collect(Collectors.toList());
           return new EntityCustomScalesValuesHistory(history.getWhoficEntityIri(), history.getProjectId(), sortedRevisions);
       });

    }
}
