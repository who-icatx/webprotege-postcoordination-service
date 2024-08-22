package edu.stanford.protege.webprotege.postcoordinationservice.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableAxisLabel;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class PostCoordinationTableConfigRepository {

    private final static String ENTITY_TYPE_KEY = "entityType";

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

    public PostCoordinationTableConfigRepository(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }


    public TableConfiguration getTableConfigurationByEntityType(String entityType) {
        Query query = Query.query(Criteria.where(ENTITY_TYPE_KEY).is(entityType));

        return mongoTemplate.findOne(query, TableConfiguration.class);
    }

    public List<TableAxisLabel> getTableAxisLabels() {
        return mongoTemplate.findAll(TableAxisLabel.class);
    }

}
