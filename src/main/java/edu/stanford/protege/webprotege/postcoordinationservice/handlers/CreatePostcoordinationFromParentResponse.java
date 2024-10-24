package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;


@JsonTypeName(CreatePostcoordinationFromParentRequest.CHANNEL)
public record CreatePostcoordinationFromParentResponse() implements Response {
    public static CreatePostcoordinationFromParentResponse create() {
        return new CreatePostcoordinationFromParentResponse();
    }
}
