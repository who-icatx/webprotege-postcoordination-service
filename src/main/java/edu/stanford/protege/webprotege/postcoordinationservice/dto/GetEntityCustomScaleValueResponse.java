package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;

import java.util.Date;

import static edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityCustomScaleValuesRequest.CHANNEL;


@JsonTypeName(CHANNEL)
public record GetEntityCustomScaleValueResponse(
        @JsonProperty("lastRevisionDate") Date lastRevisionDate,
        @JsonProperty("whoficCustomScaleValues") WhoficCustomScalesValues whoficCustomScalesValues
) implements Response {
}
