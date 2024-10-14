package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class GetEntityPostCoordinationCommandHandler implements CommandHandler<GetEntityPostCoordinationRequest, GetEntityPostCoordinationResponse> {

    private final PostCoordinationEventProcessor postCoordinationEventProcessor;


    public GetEntityPostCoordinationCommandHandler(PostCoordinationEventProcessor postCoordinationEventProcessor) {
        this.postCoordinationEventProcessor = postCoordinationEventProcessor;
    }

    @NonNull
    @Override
    public String getChannelName() {
        return GetEntityPostCoordinationRequest.CHANNEL;
    }

    @Override
    public Class<GetEntityPostCoordinationRequest> getRequestClass() {
        return GetEntityPostCoordinationRequest.class;
    }

    @Override
    public Mono<GetEntityPostCoordinationResponse> handleRequest(GetEntityPostCoordinationRequest request, ExecutionContext executionContext) {

        WhoficEntityPostCoordinationSpecification processedSpec = postCoordinationEventProcessor.fetchHistory(request.entityIRI(), request.projectId());

        return Mono.just(new GetEntityPostCoordinationResponse(request.entityIRI(), processedSpec));
    }
}
