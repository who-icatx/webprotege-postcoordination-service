package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.result.UpdateResult;
import edu.stanford.protege.webprotege.common.ChangeRequestId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.ReadWriteLockService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.PROJECT_ID;
import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.WHOFIC_ENTITY_IRI;
import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.*;
import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.*;

@Repository
public class PostCoordinationRepository {


    private static final String CHANGE_REQUEST_ID = "changeRequestId";

    private final MongoTemplate mongoTemplate;
    private final ReadWriteLockService readWriteLock;


    public PostCoordinationRepository(MongoTemplate mongoTemplate, ReadWriteLockService readWriteLock) {
        this.mongoTemplate = mongoTemplate;
        this.readWriteLock = readWriteLock;
    }

    public void bulkWriteDocuments(List<InsertOneModel<Document>> listOfInsertOneModelDocument, String collectionName) {
        var collection = mongoTemplate.getCollection(collectionName);
        collection.bulkWrite(listOfInsertOneModelDocument);
    }

    public void addSpecificationRevision(String whoficEntityIri, ProjectId projectId, PostCoordinationSpecificationRevision specificationRevision) {
        Query query = new Query();
        query.addCriteria(Criteria.where(EntityPostCoordinationHistory.WHOFIC_ENTITY_IRI).is(whoficEntityIri)
                .and(EntityPostCoordinationHistory.PROJECT_ID).is(projectId.id()));

        Update update = new Update();
        update.push(SPEC_REVISIONS, specificationRevision);

        readWriteLock.executeWriteLock(() -> {
            UpdateResult result = mongoTemplate.updateFirst(query, update, EntityPostCoordinationHistory.class, POSTCOORDINATION_HISTORY_COLLECTION);
            if (result.getMatchedCount() == 0) {
                throw new IllegalArgumentException(POSTCOORDINATION_HISTORY_COLLECTION + " not found for the given " +
                        WHOFIC_ENTITY_IRI + ":" + whoficEntityIri + " and " + PROJECT_ID +
                        ":" + projectId + ".");
            }
        });
    }

    public EntityPostCoordinationHistory saveNewSpecificationHistory(EntityPostCoordinationHistory specificationHistory) {
        return readWriteLock.executeWriteLock(() -> mongoTemplate.save(specificationHistory, POSTCOORDINATION_HISTORY_COLLECTION));
    }


    public void addCustomScalesRevision(String whoficEntityIri, ProjectId projectId, PostCoordinationCustomScalesRevision customScalesRevision) {
        Query query = new Query();
        query.addCriteria(Criteria.where(WHOFIC_ENTITY_IRI).is(whoficEntityIri)
                .and(PROJECT_ID).is(projectId.id()));

        Update update = new Update();
        update.push(CUSTOM_SCALE_REVISIONS, customScalesRevision);

        readWriteLock.executeWriteLock(() -> {
            UpdateResult result = mongoTemplate.updateFirst(query, update, EntityCustomScalesValuesHistory.class, POSTCOORDINATION_CUSTOM_SCALES_COLLECTION);
            if (result.getMatchedCount() == 0) {
                throw new IllegalArgumentException(POSTCOORDINATION_CUSTOM_SCALES_COLLECTION + " not found for the given " +
                        WHOFIC_ENTITY_IRI + ":" + whoficEntityIri + " and " + PROJECT_ID +
                        ":" + projectId + ".");
            }
        });
    }

    public EntityCustomScalesValuesHistory saveNewCustomScalesHistory(EntityCustomScalesValuesHistory entityScaleValueHistory) {
        return readWriteLock.executeWriteLock(() -> mongoTemplate.save(entityScaleValueHistory, POSTCOORDINATION_CUSTOM_SCALES_COLLECTION));
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
                }
        );

    }

    public void deletePostCoordinationCustomScalesRevision(ChangeRequestId changeRequestId, ProjectId projectId, String entityIri) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where(WHOFIC_ENTITY_IRI).is(entityIri)
                        .and(PROJECT_ID).is(projectId.value())
        );
        Update update = new Update().pull("postCoordinationCustomScalesRevisions",
                new Document("changeRequestId._id", changeRequestId.id()));

        readWriteLock.executeReadLock(() ->
                Optional.of(mongoTemplate.updateFirst(query,update, EntityCustomScalesValuesHistory.class, CUSTOM_SCALE_REVISIONS ))
        );
    }

    public void deletePostCoordinationSpecificationRevision(ChangeRequestId changeRequestId, ProjectId projectId, String entityIri) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where(WHOFIC_ENTITY_IRI).is(entityIri)
                        .and(PROJECT_ID).is(projectId.value())
        );
        Update update = new Update().pull("postCoordinationRevisions",
                new Document("changeRequestId._id", changeRequestId.id()));

        readWriteLock.executeReadLock(() -> {

            UpdateResult updateResult = mongoTemplate.updateFirst(query,update, EntityPostCoordinationHistory.class, POSTCOORDINATION_HISTORY_COLLECTION);
            System.out.println(updateResult);
            return null;
        }

        );
    }

    public void commitPostCoordinationSpecificationRevision(ChangeRequestId changeRequestId, ProjectId projectId, String entityIri){
        Query query = new Query(Criteria.where(WHOFIC_ENTITY_IRI)
                .is(entityIri)
                .and(PROJECT_ID).is(projectId.id())
                .and("postCoordinationRevisions")
                .elemMatch(
                        Criteria.where("changeRequestId").is(changeRequestId)
                                .and("commitStatus").is(CommitStatus.UNCOMMITTED.name())
                )
        );
        Update update = new Update().set("postCoordinationRevisions.$.commitStatus", CommitStatus.COMMITTED.name());

        readWriteLock.executeReadLock(() ->
                Optional.of(mongoTemplate.updateFirst(query,update, EntityPostCoordinationHistory.class, POSTCOORDINATION_HISTORY_COLLECTION))
        );
    }

    public void commitPostCoordinationCustomScalesRevision(ChangeRequestId changeRequestId, ProjectId projectId, String entityIri){
        Query query = new Query(Criteria.where(WHOFIC_ENTITY_IRI)
                .is(entityIri)
                .and(PROJECT_ID).is(projectId.id())
                .and("postCoordinationCustomScalesRevisions")
                .elemMatch(
                        Criteria.where("changeRequestId").is(changeRequestId)
                                .and("commitStatus").is(CommitStatus.UNCOMMITTED.name())
                )
        );

        Update update = new Update().set("postCoordinationCustomScalesRevisions.$.commitStatus", CommitStatus.COMMITTED.name());

        readWriteLock.executeReadLock(() ->
                Optional.of(mongoTemplate.updateFirst(query,update, EntityCustomScalesValuesHistory.class, CUSTOM_SCALE_REVISIONS ))
        );
    }
}
