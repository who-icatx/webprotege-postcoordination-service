package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;

import static edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityCustomScaleValuesRequest.CHANNEL;


@JsonTypeName(CHANNEL)
public record GetEntityCustomScaleValueResponse (@JsonProperty("entityIri")
                                                String entityIri,
                                                @JsonProperty("postCoordinationScaleValues")
                                                WhoficCustomScalesValues postCoordinationScaleValues) implements Response {
}
