package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.UploadPostCoordinationRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record UploadPostCoordinationResponse() implements Response {

}
