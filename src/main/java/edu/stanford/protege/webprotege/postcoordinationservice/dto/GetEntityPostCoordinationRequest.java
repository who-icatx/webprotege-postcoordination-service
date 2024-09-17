package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;

import static edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityPostCoordinationRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record GetEntityPostCoordinationRequest(@JsonProperty("entityIRI") String entityIRI,
                                               @JsonProperty("projectId") ProjectId projectId) implements Request<GetEntityPostCoordinationResponse> {

    public static final String CHANNEL = "webprotege.postcoordination.GetEntityPostCoordinations";

    @Override
    public String getChannel() {
        return CHANNEL;
    }

}