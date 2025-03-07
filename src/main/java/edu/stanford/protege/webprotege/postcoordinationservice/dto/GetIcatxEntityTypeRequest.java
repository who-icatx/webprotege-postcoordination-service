package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.Request;
import org.semanticweb.owlapi.model.IRI;


@JsonTypeName(GetIcatxEntityTypeRequest.CHANNEL)
public record GetIcatxEntityTypeRequest(@JsonProperty("entityIri") IRI entityIri,
                                        @JsonProperty("projectId")ProjectId projectId) implements Request<GetIcatxEntityTypeResponse> {

    public static final String CHANNEL = "webprotege.entities.GetIcatxEntityType";



    @Override
    public String getChannel() {
        return CHANNEL;
    }
}
