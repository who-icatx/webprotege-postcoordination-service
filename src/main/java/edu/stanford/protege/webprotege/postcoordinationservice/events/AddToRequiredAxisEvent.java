package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;

public class AddToRequiredAxisEvent extends PostCoordinationSpecificationEvent {

    public final static String TYPE = "AddToRequiredAxis";

    @JsonCreator
    public AddToRequiredAxisEvent(@JsonProperty("postCoordinationAxis") String postCoordinationAxis, @JsonProperty("linearizationView") String linearizationView) {
        super(postCoordinationAxis, linearizationView);
    }


    @Override
    String getType() {
        return AddToRequiredAxisEvent.TYPE;
    }

    @Override
    PostCoordinationSpecification applySpecificEvent(PostCoordinationSpecification input) {
        input.getRequiredAxes().add(this.getPostCoordinationAxis());
        return input;
    }
}
