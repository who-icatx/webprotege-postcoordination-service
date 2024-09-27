package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationScaleCustomization;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<PostCoordinationScaleCustomization> existingRequest = whoficCustomScalesValues.scaleCustomizations().stream()
                .filter(scale -> scale.getPostcoordinationAxis().equalsIgnoreCase(this.getPostCoordinationAxis()))
                .findFirst();
        if(existingRequest.isPresent()) {
            existingRequest.get().getPostcoordinationScaleValues().add(this.getPostCoordinationScaleValue());
        } else {
            List<String> scaleValues = new ArrayList<>();
            scaleValues.add(this.getPostCoordinationScaleValue());
            PostCoordinationScaleCustomization request = new PostCoordinationScaleCustomization(scaleValues, this.getPostCoordinationAxis());
            whoficCustomScalesValues.scaleCustomizations().add(request);
        }
    }
}
