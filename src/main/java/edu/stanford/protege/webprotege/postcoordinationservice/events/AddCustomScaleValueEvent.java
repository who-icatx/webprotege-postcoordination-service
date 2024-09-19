package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;

public class AddCustomScaleValueEvent extends PostCoordinationCustomScalesValueEvent {
    public final static String TYPE = "AddCustomScaleValue";

    @JsonCreator
    public AddCustomScaleValueEvent(@JsonProperty("postCoordinationAxis") String postCoordinationAxis,@JsonProperty("postCoordinationScaleValue") String postCoordinationScaleValue) {
        super(postCoordinationAxis, postCoordinationScaleValue);
    }

    @Override
    String getType() {
        return TYPE;
    }

    @Override
    public void applyEvent(WhoficCustomScalesValues whoficCustomScalesValues) {

    }
}
