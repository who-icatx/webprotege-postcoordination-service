package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.change.OntologyDocumentId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.UploadFirstCustomScalesValuesRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record UploadFirstCustomScalesValuesRequest(@JsonProperty("documentId") OntologyDocumentId documentId,
                                                   @JsonProperty("projectId") ProjectId projectId) implements Request<UploadFirstCustomScalesValuesResponse> {

    public static final String CHANNEL = "webprotege.postcoordination.ProcessFirstPostCoordinationScaleValues";


    @Override
    public String getChannel() {
        return null;
    }
}
