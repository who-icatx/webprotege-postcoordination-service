package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.*;
import org.springframework.lang.NonNull;

import java.util.Set;


@JsonTypeName(NewRevisionsEvent.CHANNEL)
public record NewRevisionsEvent(
        EventId eventId,
        ProjectId projectId,
        Set<ProjectChangeForEntity> changes
) implements ProjectEvent {
    public final static String CHANNEL = "webprotege.events.projects.uiHistory.NewRevisionsEvent";

    public static NewRevisionsEvent create(EventId eventId,
                                           ProjectId projectId,
                                           Set<ProjectChangeForEntity> changes) {
        return new NewRevisionsEvent(eventId, projectId, changes);
    }

    @NonNull
    @Override
    public ProjectId projectId() {
        return projectId;
    }

    @NonNull
    @Override
    public EventId eventId() {
        return eventId;
    }

    public Set<ProjectChangeForEntity> getProjectChanges() {
        return changes;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
