package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.common.ChangeRequestId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;

import java.util.List;

public interface NewRevisionsEventEmitterService {
    void emitNewRevisionsEventForScaleHistory(ProjectId projectId, List<EntityCustomScalesValuesHistory> entityCustomScaleHistories, ChangeRequestId changeRequestId);

    void emitNewRevisionsEventForSpecHistory(ProjectId projectId, List<EntityPostCoordinationHistory> entitySpecHistories, ChangeRequestId changeRequestId);

    void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationCustomScalesRevision entityCustomScaleRevision, ChangeRequestId changeRequestId, String commitMessage);

    void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationSpecificationRevision entitySpecRevision, ChangeRequestId changeRequestId, String commitMessage);
}
