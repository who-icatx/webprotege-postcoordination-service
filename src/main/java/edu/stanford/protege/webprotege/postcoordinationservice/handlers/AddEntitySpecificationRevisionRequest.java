package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.AddEntitySpecificationRevisionRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record AddEntitySpecificationRevisionRequest(@JsonProperty("projectId")
                                                    ProjectId projectId,
                                                    @JsonProperty("entitySpecification")
                                                    WhoficEntityPostCoordinationSpecification entitySpecification) implements Request<AddEntitySpecificationRevisionResponse> {

    public final static String CHANNEL = "webprotege.postcoordination.AddEntitySpecificationRevision";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
