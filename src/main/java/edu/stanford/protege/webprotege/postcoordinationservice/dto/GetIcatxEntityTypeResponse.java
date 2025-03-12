package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;

import java.util.List;


@JsonTypeName(GetIcatxEntityTypeRequest.CHANNEL)
public record GetIcatxEntityTypeResponse(@JsonProperty("icatxEntityTypes") List<String> icatxEntityTypes) implements Response {
}
