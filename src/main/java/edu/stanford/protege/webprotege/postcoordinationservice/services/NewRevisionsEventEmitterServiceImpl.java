package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.ipc.EventDispatcher;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NewRevisionsEventEmitterServiceImpl implements NewRevisionsEventEmitterService {

    private final EventDispatcher eventDispatcher;
    private final ProjectChangesManager projectChangesManager;

    public NewRevisionsEventEmitterServiceImpl(EventDispatcher eventDispatcher,
                                               ProjectChangesManager projectChangesManager) {
        this.eventDispatcher = eventDispatcher;
        this.projectChangesManager = projectChangesManager;
    }

    @Override
    public void emitNewRevisionsEventForScaleHistory(ProjectId projectId, List<EntityCustomScalesValuesHistory> entityCustomScaleHistories) {
        Set<ProjectChangeForEntity> changeList = projectChangesManager.getProjectChangesForCustomScaleHistories(projectId, entityCustomScaleHistories);
        NewRevisionsEvent revisionsEvent = NewRevisionsEvent.create(EventId.generate(), projectId, changeList);
        eventDispatcher.dispatchEvent(revisionsEvent);
    }

    @Override
    public void emitNewRevisionsEventForSpecHistory(ProjectId projectId, List<EntityPostCoordinationHistory> entitySpecHistories) {

    }

    @Override
    public void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationCustomScalesRevision entityCustomScaleRevision) {
        ProjectChangeForEntity projectChange = projectChangesManager.getProjectChangesForCustomScaleRevision(projectId, whoficEntityIri, entityCustomScaleRevision);
        NewRevisionsEvent revisionsEvent = NewRevisionsEvent.create(EventId.generate(), projectId, Set.of(projectChange));
        eventDispatcher.dispatchEvent(revisionsEvent);
    }

    @Override
    public void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationSpecificationRevision entitySpecRevision) {

    }
}
