package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;


@JsonTypeName(DummyHandlerRequest.CHANNEL)
public record DummyHandlerResponse() implements Response {
    public static DummyHandlerResponse create() {
        return new DummyHandlerResponse();
    }
}
