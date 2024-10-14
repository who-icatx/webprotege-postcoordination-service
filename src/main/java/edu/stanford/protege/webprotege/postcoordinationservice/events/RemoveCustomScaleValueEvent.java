package edu.stanford.protege.webprotege.postcoordinationservice.events;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationScaleCustomization;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.CustomScaleChangeVisitor;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff.ChangeOperationVisitorEx;

import javax.annotation.Nonnull;
import java.util.Optional;

public class RemoveCustomScaleValueEvent extends PostCoordinationCustomScalesValueEvent {

    public final static String TYPE = "RemoveCustomScaleValue";

    @JsonCreator
    public RemoveCustomScaleValueEvent(@JsonProperty("postCoordinationAxis") String postCoordinationAxis,
                                       @JsonProperty("postCoordinationScaleValue") String postCoordinationScaleValue) {
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
        if (existingRequest.isEmpty()) {
            throw new RuntimeException("Removing scale value from missing postCoordinationAxis " + this.getPostCoordinationAxis());
        } else {
            existingRequest.get().getPostcoordinationScaleValues().remove(this.getPostCoordinationScaleValue());
        }
    }

    public <R> R accept(@Nonnull ChangeOperationVisitorEx<R> visitor) {
        return visitor.visit(this);
    }

    public <R> R accept(@Nonnull CustomScaleChangeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
