package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;

import java.util.List;

public interface NewRevisionsEventEmitterService {
    void emitNewRevisionsEventForScaleHistory(ProjectId projectId, List<EntityCustomScalesValuesHistory> entityCustomScaleHistories);

    void emitNewRevisionsEventForSpecHistory(ProjectId projectId, List<EntityPostCoordinationHistory> entitySpecHistories);

    void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationCustomScalesRevision entityCustomScaleRevision);

    void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationSpecificationRevision entitySpecRevision);
}
