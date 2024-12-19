package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityPostCoordinationRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityPostCoordinationResponse;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.services.*;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class GetEntityPostCoordinationCommandHandler implements CommandHandler<GetEntityPostCoordinationRequest, GetEntityPostCoordinationResponse> {

    private final PostCoordinationService postCoordService;


    public GetEntityPostCoordinationCommandHandler(PostCoordinationService postCoordService) {
        this.postCoordService = postCoordService;
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

        GetEntityPostCoordinationResponse processedSpec = postCoordService.fetchHistory(request.entityIRI(), request.projectId(), "ICD");

        return Mono.just(processedSpec);
    }
}
