package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;

import java.util.List;

public record WhoficEntityPostCoordinationSpecification(String whoficEntityIri, String entityType,
                                                        List<PostCoordinationSpecification> postCoordinationSpecifications) {

    @JsonCreator
    public WhoficEntityPostCoordinationSpecification(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                                     @JsonProperty("entityType") String entityType,
                                                     @JsonProperty("postCoordinationSpecifications") List<PostCoordinationSpecification> postCoordinationSpecifications) {
        this.whoficEntityIri = whoficEntityIri;
        this.entityType = entityType;
        this.postCoordinationSpecifications = postCoordinationSpecifications;
    }
}
