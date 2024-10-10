package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import edu.stanford.protege.webprotege.postcoordinationservice.events.*;

public interface CustomScaleChangeVisitor<R> {
    R visit(AddCustomScaleValueEvent addScaleValueEvent);

    R visit(RemoveCustomScaleValueEvent removeScaleValueEvent);

    R getDefaultReturnValue();
}
