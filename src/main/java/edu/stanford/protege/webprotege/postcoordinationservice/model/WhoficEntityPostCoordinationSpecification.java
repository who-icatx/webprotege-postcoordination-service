package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import org.springframework.lang.NonNull;

import java.util.*;

public record WhoficEntityPostCoordinationSpecification(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                                        @JsonProperty("entityType") String entityType,
                                                        @JsonProperty("postcoordinationSpecifications") List<PostCoordinationSpecification> postcoordinationSpecifications) {

    @JsonCreator
    public WhoficEntityPostCoordinationSpecification(@JsonProperty("whoficEntityIri") @NonNull String whoficEntityIri,
                                                     @JsonProperty("entityType") String entityType,
                                                     @JsonProperty("postcoordinationSpecifications") List<PostCoordinationSpecification> postcoordinationSpecifications) {
        this.whoficEntityIri = whoficEntityIri;
        this.entityType = Objects.requireNonNullElse(entityType, "ICD");
        this.postcoordinationSpecifications = Objects.requireNonNullElseGet(postcoordinationSpecifications, ArrayList::new);
    }
}
