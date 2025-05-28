package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;

import javax.annotation.Nullable;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.AddEntitySpecificationRevisionRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record AddEntitySpecificationRevisionRequest(@JsonProperty("projectId")
                                                    ProjectId projectId,
                                                    @JsonProperty("entitySpecification")
                                                    WhoficEntityPostCoordinationSpecification entitySpecification,
                                                    @JsonProperty("changeRequestId") @Nullable ChangeRequestId changeRequestId,
                                                    @JsonProperty("commitMessage") @Nullable String commitMessage) implements Request<AddEntitySpecificationRevisionResponse> {

    public final static String CHANNEL = "webprotege.postcoordination.AddEntitySpecificationRevision";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
