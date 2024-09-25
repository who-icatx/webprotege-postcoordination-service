package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationScaleCustomization;

import java.util.List;

public record WhoficCustomScalesValues(String whoficEntityIri,
                                       List<PostCoordinationScaleCustomization> scaleCustomizations) {

    @JsonCreator
    public WhoficCustomScalesValues(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                    @JsonProperty("scaleCustomizations") List<PostCoordinationScaleCustomization> scaleCustomizations) {
        this.whoficEntityIri = whoficEntityIri;
        this.scaleCustomizations = scaleCustomizations;
    }


}
