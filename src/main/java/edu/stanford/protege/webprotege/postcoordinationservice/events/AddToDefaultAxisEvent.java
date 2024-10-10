package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;

public class AddToDefaultAxisEvent extends PostCoordinationSpecificationEvent {

    public final static String TYPE = "AddToDefaultAxis";

    @JsonCreator
    public AddToDefaultAxisEvent(@JsonProperty("postCoordinationAxis") String postCoordinationAxis, @JsonProperty("linearizationView") String linearizationView) {
        super(postCoordinationAxis, linearizationView);
    }


    @Override
    public String getType() {
        return AddToDefaultAxisEvent.TYPE;
    }

    @Override
    PostCoordinationSpecification applySpecificEvent(PostCoordinationSpecification input) {
        input.getDefaultAxes().add(this.getPostCoordinationAxis());
        return input;
    }
}
