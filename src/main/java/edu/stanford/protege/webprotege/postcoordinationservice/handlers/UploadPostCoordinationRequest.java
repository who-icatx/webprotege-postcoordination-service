package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.change.OntologyDocumentId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;

public record UploadPostCoordinationRequest( @JsonProperty("documentId") OntologyDocumentId documentId,
                                             @JsonProperty("projectId") ProjectId projectId) implements Request<UploadPostCoordinationResponse> {
    public static final String CHANNEL = "webprotege.postcoordination.ProcessUploadedPostCoordination";

    @JsonCreator
    public UploadPostCoordinationRequest {
    }

    public OntologyDocumentId getDocumentId() {
        return documentId;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
