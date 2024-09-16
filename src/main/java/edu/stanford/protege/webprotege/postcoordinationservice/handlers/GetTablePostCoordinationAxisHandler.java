package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableAxisLabel;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableConfiguration;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationTableConfigRepository;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;


@WebProtegeHandler
public class GetTablePostCoordinationAxisHandler implements CommandHandler<GetTablePostCoordinationAxisRequest, GetTablePostCoordinationAxisResponse> {


    private final PostCoordinationTableConfigRepository tableConfigRepository;

    public GetTablePostCoordinationAxisHandler(PostCoordinationTableConfigRepository tableConfigRepository) {
        this.tableConfigRepository = tableConfigRepository;
    }


    @NotNull
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
