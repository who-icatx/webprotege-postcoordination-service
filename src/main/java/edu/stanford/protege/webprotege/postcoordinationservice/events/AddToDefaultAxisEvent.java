package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecificationRequest;

public class AddToDefaultAxisEvent extends PostCoordinationEvent {

    public final static String TYPE = "AddToDefaultAxis";

    @JsonCreator
    public AddToDefaultAxisEvent(@JsonProperty("postCoordinationAxis") String postCoordinationAxis, @JsonProperty("linearizationView") String linearizationView) {
        super(postCoordinationAxis, linearizationView);
    }


    @Override
    String getType() {
        return AddToDefaultAxisEvent.TYPE;
    }

    @Override
    PostCoordinationSpecificationRequest applySpecificEvent(PostCoordinationSpecificationRequest input) {
        return null;
    }
}
