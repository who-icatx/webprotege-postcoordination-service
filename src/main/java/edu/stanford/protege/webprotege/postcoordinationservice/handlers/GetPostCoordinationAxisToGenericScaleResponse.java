package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.postcoordinationservice.model.AxisToGenericScale;

import java.util.List;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.GetPostCoordinationAxisToGenericScaleRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record GetPostCoordinationAxisToGenericScaleResponse(
        @JsonProperty("postcoordinationAxisToGenericScales") List<AxisToGenericScale> postcoordinationAxisToGenericScales
) implements Response {

    public static GetPostCoordinationAxisToGenericScaleResponse create(List<AxisToGenericScale> postcoordinationAxisToGenericScales){
        return new GetPostCoordinationAxisToGenericScaleResponse(postcoordinationAxisToGenericScales);
    }
}
