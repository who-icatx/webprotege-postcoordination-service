package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;

public class AddToNotAllowedAxisEvent extends PostCoordinationSpecificationEvent {

    public final static String TYPE = "AddToNotAllowedAxis";

    @JsonCreator
    public AddToNotAllowedAxisEvent(@JsonProperty("postCoordinationAxis") String postCoordinationAxis, @JsonProperty("linearizationView") String linearizationView) {
        super(postCoordinationAxis, linearizationView);
    }


    @Override
    String getType() {
        return AddToNotAllowedAxisEvent.TYPE;
    }

    @Override
    PostCoordinationSpecification applySpecificEvent(PostCoordinationSpecification input) {
        input.getNotAllowedAxes().add(this.getPostCoordinationAxis());
        return input;
    }

}
