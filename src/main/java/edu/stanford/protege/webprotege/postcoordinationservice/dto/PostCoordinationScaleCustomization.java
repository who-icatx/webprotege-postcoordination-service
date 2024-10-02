package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.events.EventProcessableParameter;

import java.util.List;

public class PostCoordinationScaleCustomization extends EventProcessableParameter {

    private final List<String> postcoordinationScaleValues;
    private final String postcoordinationAxis;


    @JsonCreator
    public PostCoordinationScaleCustomization(@JsonProperty("postcoordinationScaleValues") List<String> postcoordinationScaleValues,
                                              @JsonProperty("postcoordinationAxis") String postcoordinationAxis) {
        this.postcoordinationScaleValues = postcoordinationScaleValues;
        this.postcoordinationAxis = postcoordinationAxis;
    }


    @JsonProperty("postcoordinationScaleValues")
    public List<String> getPostcoordinationScaleValues() {
        return postcoordinationScaleValues;
    }

    @JsonProperty("postcoordinationAxis")
    public String getPostcoordinationAxis() {
        return postcoordinationAxis;
    }
}
