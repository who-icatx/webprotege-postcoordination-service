package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;

public class AddToAllowedAxisEvent extends PostCoordinationSpecificationEvent {


    public final static String TYPE = "AddToAllowedAxis";

    @JsonCreator
    public AddToAllowedAxisEvent(@JsonProperty("postCoordinationAxis") String postCoordinationAxis,@JsonProperty("linearizationView") String linearizationView) {
        super(postCoordinationAxis, linearizationView);
    }


    @Override
    String getType() {
        return AddToAllowedAxisEvent.TYPE;
    }

    @Override
    PostCoordinationSpecification applySpecificEvent(PostCoordinationSpecification input) {
        input.getAllowedAxes().add(this.getPostCoordinationAxis());
        return input;
    }

}
