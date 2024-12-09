package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.EventHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.events.EntityUpdateFailedEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.services.RevisionCommitService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class EventUpdateFailedHandler implements EventHandler<EntityUpdateFailedEvent> {

    private final RevisionCommitService revisionCommitService;

    public EventUpdateFailedHandler(RevisionCommitService revisionCommitService) {
        this.revisionCommitService = revisionCommitService;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return EntityUpdateFailedEvent.CHANNEL;
    }

    @NotNull
    @Override
    public String getHandlerName() {
        return EventUpdateFailedHandler.class.getName();
    }

    @Override
    public Class<EntityUpdateFailedEvent> getEventClass() {
        return EntityUpdateFailedEvent.class;
    }

    @Override
    public void handleEvent(EntityUpdateFailedEvent event) {
        revisionCommitService.rollbackRevision(event.changeRequestId(), event.projectId(), event.entityIri());
    }
}
