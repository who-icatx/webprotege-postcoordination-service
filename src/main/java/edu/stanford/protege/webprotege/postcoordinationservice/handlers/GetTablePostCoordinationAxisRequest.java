package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.Request;

@JsonTypeName(GetTablePostCoordinationAxisRequest.CHANNEL)
public record GetTablePostCoordinationAxisRequest(
        String entityType) implements Request<GetTablePostCoordinationAxisResponse> {
    public final static String CHANNEL = "webprotege.postcoordination.GetTablePostCoordinationAxis";

    @JsonCreator
    public GetTablePostCoordinationAxisRequest(@JsonProperty("entityType") String entityType) {
        this.entityType = entityType;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
