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
    public void emitNewRevisionsEventForScaleHistory(ProjectId projectId, List<EntityCustomScalesValuesHistory> entityCustomScaleHistories, ChangeRequestId changeRequestId) {
        Set<ProjectChangeForEntity> changeList = projectChangesManager.getProjectChangesForCustomScaleHistories(projectId, entityCustomScaleHistories);
        NewRevisionsEvent revisionsEvent = NewRevisionsEvent.create(EventId.generate(), projectId, changeList, changeRequestId);
        eventDispatcher.dispatchEvent(revisionsEvent);
    }

    @Override
    public void emitNewRevisionsEventForSpecHistory(ProjectId projectId, List<EntityPostCoordinationHistory> entitySpecHistories, ChangeRequestId changeRequestId) {
        Set<ProjectChangeForEntity> changeList = projectChangesManager.getProjectChangesForSpecHistories(projectId, entitySpecHistories);
        NewRevisionsEvent revisionsEvent = NewRevisionsEvent.create(EventId.generate(), projectId, changeList, changeRequestId);
        eventDispatcher.dispatchEvent(revisionsEvent);
    }

    @Override
    public void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationCustomScalesRevision entityCustomScaleRevision, ChangeRequestId changeRequestId) {
        ProjectChangeForEntity projectChange = projectChangesManager.getProjectChangesForCustomScaleRevision(projectId, whoficEntityIri, entityCustomScaleRevision);
        NewRevisionsEvent revisionsEvent = NewRevisionsEvent.create(EventId.generate(), projectId, Set.of(projectChange), changeRequestId);
        eventDispatcher.dispatchEvent(revisionsEvent);
    }

    @Override
    public void emitNewRevisionsEvent(ProjectId projectId, String whoficEntityIri, PostCoordinationSpecificationRevision entitySpecRevision, ChangeRequestId changeRequestId) {
        ProjectChangeForEntity projectChange = projectChangesManager.getProjectChangesForSpecRevision(projectId, whoficEntityIri, entitySpecRevision);
        NewRevisionsEvent revisionsEvent = NewRevisionsEvent.create(EventId.generate(), projectId, Set.of(projectChange), changeRequestId);
        eventDispatcher.dispatchEvent(revisionsEvent);
    }
}
