package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityPostCoordinationRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityPostCoordinationResponse;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Collections;

@WebProtegeHandler
public class GetEntityPostCoordinationCommandHandler implements CommandHandler<GetEntityPostCoordinationRequest, GetEntityPostCoordinationResponse> {

    private final PostCoordinationService postCoordinationService;

    private final PostCoordinationEventProcessor postCoordinationEventProcessor;

    public GetEntityPostCoordinationCommandHandler(PostCoordinationService postCoordinationService, PostCoordinationEventProcessor postCoordinationEventProcessor) {
        this.postCoordinationService = postCoordinationService;
        this.postCoordinationEventProcessor = postCoordinationEventProcessor;
    }

    @NotNull
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

        WhoficEntityPostCoordinationSpecification processedSpec =
                this.postCoordinationService.getExistingHistoryOrderedByRevision(request.entityIRI(), request.projectId())
                        .map(postCoordinationEventProcessor::processHistory)
                        .orElseGet(() -> new WhoficEntityPostCoordinationSpecification(request.entityIRI(), null, Collections.emptyList()));

        return Mono.just(new GetEntityPostCoordinationResponse(request.entityIRI(), processedSpec));
    }
}
