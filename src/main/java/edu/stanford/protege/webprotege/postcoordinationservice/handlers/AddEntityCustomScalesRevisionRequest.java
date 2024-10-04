package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.AddEntityCustomScalesRevisionRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record AddEntityCustomScalesRevisionRequest(@JsonProperty("projectId")
                                                   ProjectId projectId,
                                                   @JsonProperty("entityCustomScaleValues")
                                                   WhoficCustomScalesValues entityCustomScaleValues) implements Request<AddEntityCustomScalesRevisionResponse> {

    public final static String CHANNEL = "webprotege.postcoordination.AddEntityCustomScalesRevision";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
