package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;
import org.semanticweb.owlapi.model.IRI;

import java.util.List;

@JsonTypeName(GetTablePostCoordinationAxisRequest.CHANNEL)
public record GetTablePostCoordinationAxisRequest(IRI entityIri, ProjectId projectId) implements Request<GetTablePostCoordinationAxisResponse> {
    public final static String CHANNEL = "webprotege.postcoordination.GetTablePostCoordinationAxis";

    @JsonCreator
    public GetTablePostCoordinationAxisRequest(@JsonProperty("entityIri") IRI entityIri, @JsonProperty("projectId") ProjectId projectId) {
        this.entityIri = entityIri;
        this.projectId = projectId;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
