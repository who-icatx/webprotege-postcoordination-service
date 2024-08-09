package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Request;

@JsonTypeName(DummyHandlerRequest.CHANNEL)
public record DummyHandlerRequest(
) implements Request<DummyHandlerResponse> {

    public final static String CHANNEL = "webprotege.postcoordination.DummyHandler";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
