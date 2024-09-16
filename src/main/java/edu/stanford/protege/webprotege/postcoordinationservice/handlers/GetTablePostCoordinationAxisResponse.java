package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableAxisLabel;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableConfiguration;

import java.util.List;

@JsonTypeName(GetTablePostCoordinationAxisRequest.CHANNEL)
public record GetTablePostCoordinationAxisResponse(TableConfiguration tableConfiguration,
                                                   List<TableAxisLabel> labels) implements Response {

}
