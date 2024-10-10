package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff;

import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import jakarta.annotation.Nonnull;

public interface ChangeOperationVisitorEx<R> {

    default R getDefaultReturnValue() {
        return null;
    }

    default R visit(@Nonnull AddCustomScaleValueEvent addScaleValueEvent) {
        return getDefaultReturnValue();
    }

    default R visit(@Nonnull RemoveCustomScaleValueEvent removeScaleValueEvent) {
        return getDefaultReturnValue();
    }
}