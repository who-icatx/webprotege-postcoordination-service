package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;

import static edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityPostCoordinationRequest.CHANNEL;

@JsonTypeName(CHANNEL)
public record GetEntityPostCoordinationResponse(@JsonProperty("entityIri")
                                                String entityIri,
                                                @JsonProperty("postCoordinationSpecification")
                                                WhoficEntityPostCoordinationSpecification postCoordinationSpecification) implements Response {
}
