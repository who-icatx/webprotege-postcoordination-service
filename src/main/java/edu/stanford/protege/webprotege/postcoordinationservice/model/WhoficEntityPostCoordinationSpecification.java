package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record WhoficEntityPostCoordinationSpecification(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                                        @JsonProperty("entityType") String entityType,
                                                        @JsonProperty("postcoordinationSpecifications") List<PostCoordinationSpecification> postcoordinationSpecifications) {

    public WhoficEntityPostCoordinationSpecification(@NotNull String whoficEntityIri,
                                                     String entityType,
                                                     List<PostCoordinationSpecification> postcoordinationSpecifications) {
        this.whoficEntityIri = whoficEntityIri;
        this.entityType = Objects.requireNonNullElse(entityType, "ICD");
        this.postcoordinationSpecifications = Objects.requireNonNullElseGet(postcoordinationSpecifications, ArrayList::new);
    }

    @JsonCreator
    public static WhoficEntityPostCoordinationSpecification create(@JsonProperty("whoficEntityIri") @NotNull String whoficEntityIri,
                                                                   @JsonProperty("entityType") String entityType,
                                                                   @JsonProperty("postcoordinationSpecifications") List<PostCoordinationSpecification> postcoordinationSpecifications) {
        return new WhoficEntityPostCoordinationSpecification(whoficEntityIri,
                entityType,
                postcoordinationSpecifications);
    }
}
