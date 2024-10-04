package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Request;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.GetPostCoordinationAxisToGenericScaleRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record GetPostCoordinationAxisToGenericScaleRequest() implements Request<GetPostCoordinationAxisToGenericScaleResponse> {
    public final static String CHANNEL = "webprotege.postcoordination.GetPostCoordinationAxisToGenericScale";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
