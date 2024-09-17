package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.events.EventProcessableParameter;

import java.util.ArrayList;
import java.util.List;

public class PostCoordinationSpecificationRequest extends EventProcessableParameter {

    private final String linearizationView;

    private final List<String> allowedAxes;

    private final List<String> defaultAxes;

    private final List<String> notAllowedAxes;

    private final List<String> requiredAxes;

    @JsonCreator
    public PostCoordinationSpecificationRequest(@JsonProperty("linearizationView") String linearizationView,
                                                @JsonProperty("allowedAxes") List<String> allowedAxes,
                                                @JsonProperty("defaultAxes") List<String> defaultAxes,
                                                @JsonProperty("notAllowedAxes") List<String> notAllowedAxes,
                                                @JsonProperty("requiredAxes") List<String> requiredAxes) {
        this.linearizationView = linearizationView;
        this.allowedAxes = allowedAxes == null ? new ArrayList<>() : allowedAxes;
        this.defaultAxes = defaultAxes == null ? new ArrayList<>() : defaultAxes;
        this.notAllowedAxes = notAllowedAxes == null ? new ArrayList<>() : notAllowedAxes;
        this.requiredAxes = requiredAxes == null ? new ArrayList<>() : requiredAxes;
    }


    public String getLinearizationView() {
        return linearizationView;
    }

    public List<String> getAllowedAxes() {
        return allowedAxes;
    }

    public List<String> getDefaultAxes() {
        return defaultAxes;
    }

    public List<String> getNotAllowedAxes() {
        return notAllowedAxes;
    }

    public List<String> getRequiredAxes() {
        return requiredAxes;
    }

}
