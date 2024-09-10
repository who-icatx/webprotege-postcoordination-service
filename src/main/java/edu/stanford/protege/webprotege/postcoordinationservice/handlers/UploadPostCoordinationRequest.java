package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.stanford.protege.webprotege.change.OntologyDocumentId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;

public class UploadPostCoordinationRequest implements Request<UploadPostCoordinationResponse> {
    public static final String CHANNEL = "webprotege.postcoordination.ProcessUploadedPostCoordination";

    @JsonProperty("documentId")
    private  final OntologyDocumentId documentId;
    @JsonProperty("projectId")
    private final ProjectId projectId;


    @JsonCreator
    public UploadPostCoordinationRequest(@JsonProperty("documentId") OntologyDocumentId documentId, @JsonProperty("projectId") ProjectId projectId) {
        this.documentId = documentId;
        this.projectId = projectId;
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
