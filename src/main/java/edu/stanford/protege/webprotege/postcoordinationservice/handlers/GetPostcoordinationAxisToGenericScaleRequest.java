package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Request;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.GetPostcoordinationAxisToGenericScaleRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record GetPostcoordinationAxisToGenericScaleRequest() implements Request<GetPostcoordinationAxisToGenericScaleResponse> {
    public final static String CHANNEL = "webprotege.postcoordination.GetPostcoordinationAxisToGenericScale";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
