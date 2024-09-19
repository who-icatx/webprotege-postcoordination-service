package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import com.mongodb.client.model.InsertOneModel;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory;
import edu.stanford.protege.webprotege.postcoordinationservice.services.ReadWriteLockService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public void bulkWriteDocuments(List<InsertOneModel<Document>> listOfInsertOneModelDocument) {
        readWriteLock.executeWriteLock(() -> {
            var collection = mongoTemplate.getCollection(POSTCOORDINATION_HISTORY_COLLECTION);
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

}
