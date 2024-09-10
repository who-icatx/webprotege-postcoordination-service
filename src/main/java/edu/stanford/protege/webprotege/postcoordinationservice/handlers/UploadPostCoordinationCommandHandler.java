package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;


@WebProtegeHandler
public class UploadPostCoordinationCommandHandler implements CommandHandler<UploadPostCoordinationRequest, UploadPostCoordinationResponse> {


    private final PostCoordinationService postCoordinationService;

    public UploadPostCoordinationCommandHandler(PostCoordinationService postCoordinationService) {
        this.postCoordinationService = postCoordinationService;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return UploadPostCoordinationRequest.CHANNEL;
    }

    @Override
    public Class<UploadPostCoordinationRequest> getRequestClass() {
        return UploadPostCoordinationRequest.class;
    }

    @Override
    public Mono<UploadPostCoordinationResponse> handleRequest(UploadPostCoordinationRequest request, ExecutionContext executionContext) {
        postCoordinationService.createFirstImport(request.getDocumentId().id(), request.getProjectId(), executionContext.userId());
        return Mono.just(new UploadPostCoordinationResponse());
    }
}
