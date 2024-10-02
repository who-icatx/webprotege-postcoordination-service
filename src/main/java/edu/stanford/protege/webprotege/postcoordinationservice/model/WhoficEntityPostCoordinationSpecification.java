package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record WhoficEntityPostCoordinationSpecification(@JsonProperty("whoficEntityIri") String whoficEntityIri, @JsonProperty("entityType") String entityType,
                                                        @JsonProperty("postcoordinationSpecifications") List<PostCoordinationSpecification> postcoordinationSpecifications) {

    @JsonCreator
    public WhoficEntityPostCoordinationSpecification(@JsonProperty("whoficEntityIri") @NotNull String whoficEntityIri,
                                                     @JsonProperty("entityType") String entityType,
                                                     @JsonProperty("postcoordinationSpecifications") List<PostCoordinationSpecification> postcoordinationSpecifications) {
        this.whoficEntityIri = whoficEntityIri;
        this.entityType = Objects.requireNonNullElse(entityType, "ICD");
        this.postcoordinationSpecifications = Objects.requireNonNullElseGet(postcoordinationSpecifications, ArrayList::new);
    }
}
