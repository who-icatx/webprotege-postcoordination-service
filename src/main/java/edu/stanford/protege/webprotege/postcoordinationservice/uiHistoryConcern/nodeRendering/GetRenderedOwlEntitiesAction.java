package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.nodeRendering;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.dispatch.ProjectAction;

import javax.annotation.Nonnull;
import java.util.Set;

@JsonTypeName(GetRenderedOwlEntitiesAction.CHANNEL)
public record GetRenderedOwlEntitiesAction(
        @JsonProperty("entityIris") Set<String> entityIris,
        @JsonProperty("projectId") ProjectId projectId
) implements ProjectAction<GetRenderedOwlEntitiesResult> {

    public static final String CHANNEL = "webprotege.entities.RenderedOwlEntities";

    @Override
    @Nonnull
    public ProjectId projectId() {
        return projectId;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }

    public static GetRenderedOwlEntitiesAction create(Set<String> entityIris, ProjectId projectId) {
        return new GetRenderedOwlEntitiesAction(entityIris, projectId);
    }
}
