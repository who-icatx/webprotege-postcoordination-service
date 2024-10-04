package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostcoordinationAxisToGenericScale;

import java.util.List;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.GetPostcoordinationAxisToGenericScaleRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record GetPostcoordinationAxisToGenericScaleResponse(
        @JsonProperty("postcoordinationAxisToGenericScales") List<PostcoordinationAxisToGenericScale> postcoordinationAxisToGenericScales
) implements Response {

    @JsonCreator
    public static GetPostcoordinationAxisToGenericScaleResponse create(@JsonProperty("postcoordinationAxisToGenericScales") List<PostcoordinationAxisToGenericScale> postcoordinationAxisToGenericScales) {
        return new GetPostcoordinationAxisToGenericScaleResponse(postcoordinationAxisToGenericScales);
    }
}
