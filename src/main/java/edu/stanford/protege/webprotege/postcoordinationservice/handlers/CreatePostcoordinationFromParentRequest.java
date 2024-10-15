package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.*;
import org.semanticweb.owlapi.model.IRI;

@JsonTypeName(CreatePostcoordinationFromParentRequest.CHANNEL)
public record CreatePostcoordinationFromParentRequest(
        @JsonProperty("newEntityIri") IRI newEntityIri,
        @JsonProperty("parentEntityIri") IRI parentEntityIri,
        @JsonProperty("projectId") ProjectId projectId
) implements Request<CreatePostcoordinationFromParentResponse> {

    public final static String CHANNEL = "webprotege.postcoordination.CreateFromParentEntity";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
