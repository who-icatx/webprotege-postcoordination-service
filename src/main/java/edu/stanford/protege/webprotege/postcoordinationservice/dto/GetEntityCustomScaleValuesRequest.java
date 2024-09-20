package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;

import static edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityCustomScaleValuesRequest.CHANNEL;


@JsonTypeName(CHANNEL)
public record GetEntityCustomScaleValuesRequest(@JsonProperty("entityIRI") String entityIRI,
                                                @JsonProperty("projectId") ProjectId projectId) implements Request<GetEntityCustomScaleValueResponse> {

    public static final String CHANNEL = "webprotege.postcoordination.GetEntityScaleValues";

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
