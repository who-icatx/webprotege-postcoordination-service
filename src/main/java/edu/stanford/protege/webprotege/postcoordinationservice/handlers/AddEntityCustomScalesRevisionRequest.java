package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;

import javax.annotation.Nullable;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.AddEntityCustomScalesRevisionRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record AddEntityCustomScalesRevisionRequest(@JsonProperty("projectId")
                                                   ProjectId projectId,
                                                   @JsonProperty("entityCustomScaleValues")
                                                   WhoficCustomScalesValues entityCustomScaleValues,
                                                   @JsonProperty("changeRequestId") @Nullable ChangeRequestId changeRequestId,
                                                   @JsonProperty("commitMessage") @Nullable String commitMessage) implements Request<AddEntityCustomScalesRevisionResponse> {

    public final static String CHANNEL = "webprotege.postcoordination.AddEntityCustomScalesRevision";

    @JsonCreator
    public static AddEntityCustomScalesRevisionRequest create(@JsonProperty("projectId")
                                                              ProjectId projectId,
                                                              @JsonProperty("entityCustomScaleValues")
                                                              WhoficCustomScalesValues entityCustomScaleValues,
                                                              @JsonProperty("changeRequestId") @Nullable ChangeRequestId changeRequestId,
                                                              @JsonProperty("commitMessage") String commitMessage) {
        return new AddEntityCustomScalesRevisionRequest(projectId, entityCustomScaleValues, changeRequestId, commitMessage);
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }


}
