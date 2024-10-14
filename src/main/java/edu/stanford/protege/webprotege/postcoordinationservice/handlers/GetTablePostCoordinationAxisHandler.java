package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationTableConfigRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.List;


@WebProtegeHandler
public class GetTablePostCoordinationAxisHandler implements CommandHandler<GetTablePostCoordinationAxisRequest, GetTablePostCoordinationAxisResponse> {


    private final PostCoordinationTableConfigRepository tableConfigRepository;

    public GetTablePostCoordinationAxisHandler(PostCoordinationTableConfigRepository tableConfigRepository) {
        this.tableConfigRepository = tableConfigRepository;
    }


    @NonNull
    @Override
    public String getChannelName() {
        return GetTablePostCoordinationAxisRequest.CHANNEL;
    }

    @Override
    public Class<GetTablePostCoordinationAxisRequest> getRequestClass() {
        return GetTablePostCoordinationAxisRequest.class;
    }

    @Override
    public Mono<GetTablePostCoordinationAxisResponse> handleRequest(GetTablePostCoordinationAxisRequest request, ExecutionContext executionContext) {
        TableConfiguration tableConfiguration = tableConfigRepository.getTableConfigurationByEntityType(request.entityType());
        List<TableAxisLabel> labels = tableConfigRepository.getTableAxisLabels();

        return Mono.just(new GetTablePostCoordinationAxisResponse(tableConfiguration, labels));
    }
}
