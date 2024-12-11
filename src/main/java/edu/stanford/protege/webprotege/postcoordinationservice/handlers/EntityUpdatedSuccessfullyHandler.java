package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.EventHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.events.EntityUpdatedSuccessfullyEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.services.RevisionCommitService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class EntityUpdatedSuccessfullyHandler implements EventHandler<EntityUpdatedSuccessfullyEvent> {


    private final RevisionCommitService revisionCommitService;

    public EntityUpdatedSuccessfullyHandler(RevisionCommitService revisionCommitService) {
        this.revisionCommitService = revisionCommitService;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return EntityUpdatedSuccessfullyEvent.CHANNEL;
    }

    @NotNull
    @Override
    public String getHandlerName() {
        return this.getClass().getName();
    }

    @Override
    public Class<EntityUpdatedSuccessfullyEvent> getEventClass() {
        return EntityUpdatedSuccessfullyEvent.class;
    }

    @Override
    public void handleEvent(EntityUpdatedSuccessfullyEvent event) {
        revisionCommitService.commitRevision(event.changeRequestId(), event.projectId(), event.entityIri());
    }
}
