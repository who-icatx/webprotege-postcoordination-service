package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecificationRequest;

import java.util.List;

public class WhoficEntityPostCoordinationSpecification {

    private final String whoficEntityIri;

    private final String entityType;
    private final List<PostCoordinationSpecificationRequest> postcoordinationSpecifications;


    @JsonCreator
    public WhoficEntityPostCoordinationSpecification(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                                     @JsonProperty("entityType") String entityType,
                                                     @JsonProperty("postcoordinationSpecifications") List<PostCoordinationSpecificationRequest> postcoordinationSpecifications) {
        this.whoficEntityIri = whoficEntityIri;
        this.entityType = entityType;
        this.postcoordinationSpecifications = postcoordinationSpecifications;
    }

    public String getWhoficEntityIri() {
        return whoficEntityIri;
    }

    public List<PostCoordinationSpecificationRequest> getPostCoordinationSpecifications() {
        return postcoordinationSpecifications;
    }

    public String getEntityType() {
        return entityType;
    }
}
