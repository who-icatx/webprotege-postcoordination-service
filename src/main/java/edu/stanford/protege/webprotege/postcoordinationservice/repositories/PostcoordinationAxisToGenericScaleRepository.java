package edu.stanford.protege.webprotege.postcoordinationservice.repositories;

import edu.stanford.protege.webprotege.postcoordinationservice.model.PostcoordinationAxisToGenericScale;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class PostcoordinationAxisToGenericScaleRepository {


    private final MongoTemplate mongoTemplate;


    public PostcoordinationAxisToGenericScaleRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable("postCoordAxisToGenericScale")
    public List<PostcoordinationAxisToGenericScale> getPostCoordAxisToGenericScale() {
        return mongoTemplate.findAll(PostcoordinationAxisToGenericScale.class);
    }

}
