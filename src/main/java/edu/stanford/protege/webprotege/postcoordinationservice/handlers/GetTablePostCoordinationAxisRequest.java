package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.Request;

@JsonTypeName(GetTablePostCoordinationAxisRequest.CHANNEL)
public class GetTablePostCoordinationAxisRequest implements Request<GetTablePostCoordinationAxisResponse> {
    public final static String CHANNEL = "webprotege.postcoordination.GetTablePostCoordinationAxis";

    private final String entityType;


    public GetTablePostCoordinationAxisRequest(@JsonProperty("entityType") String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
