package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.UploadFirstCustomScalesValuesRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record UploadFirstCustomScalesValuesResponse() implements Response {
}
