package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.events.EventProcessableParameter;

import java.util.List;

public class PostCoordinationCustomScalesRequest extends EventProcessableParameter {

    private final List<String> postCoordinationScalesValues;
    private final String postCoordinationAxis;


    @JsonCreator
    public PostCoordinationCustomScalesRequest(@JsonProperty("postcoordinationScaleValues") List<String> postCoordinationScalesValues,
                                               @JsonProperty("postcoordinationAxis") String postCoordinationAxis) {
        this.postCoordinationScalesValues = postCoordinationScalesValues;
        this.postCoordinationAxis = postCoordinationAxis;
    }

    public List<String> getPostCoordinationScalesValues() {
        return postCoordinationScalesValues;
    }

    public String getPostCoordinationAxis() {
        return postCoordinationAxis;
    }
}
