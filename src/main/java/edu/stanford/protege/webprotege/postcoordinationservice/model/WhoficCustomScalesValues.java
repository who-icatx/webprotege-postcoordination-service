package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationScaleCustomization;

import java.util.List;

public record WhoficCustomScalesValues(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                       @JsonProperty("scaleCustomizations") List<PostCoordinationScaleCustomization> scaleCustomizations) {

    @JsonCreator
    public static WhoficCustomScalesValues create(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                                  @JsonProperty("scaleCustomizations") List<PostCoordinationScaleCustomization> scaleCustomizations) {
        return new WhoficCustomScalesValues(whoficEntityIri, scaleCustomizations);
    }
}
