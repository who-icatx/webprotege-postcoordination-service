package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;


@WebProtegeHandler
public class GetEntityCustomScaleValuesCommandHandler implements CommandHandler<GetEntityCustomScaleValuesRequest, GetEntityCustomScaleValueResponse> {


    private final PostCoordinationEventProcessor postCoordinationEventProcessor;

    public GetEntityCustomScaleValuesCommandHandler(PostCoordinationEventProcessor postCoordinationEventProcessor) {
        this.postCoordinationEventProcessor = postCoordinationEventProcessor;
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
        WhoficCustomScalesValues processedScales = postCoordinationEventProcessor.fetchCustomScalesHistory(request.entityIRI(), request.projectId());

        return Mono.just(new GetEntityCustomScaleValueResponse(processedScales));
    }
}
