package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationCustomScalesRequest;

import java.util.List;

public class WhoficCustomScalesValues {

    private final String whoficEntityIri;

    private final List<PostCoordinationCustomScalesRequest> scaleCustomizations;

    @JsonCreator
    public WhoficCustomScalesValues(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                    @JsonProperty("scaleCustomizations") List<PostCoordinationCustomScalesRequest> scaleCustomizations) {
        this.whoficEntityIri = whoficEntityIri;
        this.scaleCustomizations = scaleCustomizations;
    }
}
