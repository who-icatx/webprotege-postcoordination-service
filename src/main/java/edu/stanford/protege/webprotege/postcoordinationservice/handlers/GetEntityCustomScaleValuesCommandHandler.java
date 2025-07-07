package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;


@WebProtegeHandler
public class GetEntityCustomScaleValuesCommandHandler implements CommandHandler<GetEntityCustomScaleValuesRequest, GetEntityCustomScaleValueResponse> {


    private final PostCoordinationService postCoordService;

    public GetEntityCustomScaleValuesCommandHandler(PostCoordinationService postCoordService) {
        this.postCoordService = postCoordService;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return GetEntityCustomScaleValuesRequest.CHANNEL;
    }

    @Override
    public Class<GetEntityCustomScaleValuesRequest> getRequestClass() {
        return GetEntityCustomScaleValuesRequest.class;
    }

    @Override
    public Mono<GetEntityCustomScaleValueResponse> handleRequest(GetEntityCustomScaleValuesRequest request, ExecutionContext executionContext) {
        GetEntityCustomScaleValueResponse response = postCoordService.fetchCustomScalesHistory(request.entityIRI(), request.projectId(), executionContext);

        return Mono.just(response);
    }
}
