package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import com.fasterxml.jackson.annotation.JsonTypeName;
import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableAxisLabel;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableConfiguration;

import java.util.List;

@JsonTypeName(GetTablePostCoordinationAxisRequest.CHANNEL)
public class GetTablePostCoordinationAxisResponse implements Response {

    private final TableConfiguration tableConfiguration;

    private final List<TableAxisLabel> labels;

    public GetTablePostCoordinationAxisResponse(TableConfiguration tableConfiguration, List<TableAxisLabel> labels) {
        this.tableConfiguration = tableConfiguration;
        this.labels = labels;
    }

    public TableConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public List<TableAxisLabel> getLabels() {
        return labels;
    }
}
