package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;

import java.util.List;


@JsonTypeName(LinearizationDefinitionRequest.CHANNEL)
public record LinearizationDefinitionResponse(
        @JsonProperty("definitionList") List<LinearizationDefinition> definitionList) implements Response {

}
