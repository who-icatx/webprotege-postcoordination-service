package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;


@WebProtegeHandler
public class UploadPostCoordinationCommandHandler implements CommandHandler<UploadPostCoordinationRequest, UploadPostCoordinationResponse> {

    private final static Logger LOGGER = LoggerFactory.getLogger(UploadPostCoordinationCommandHandler.class);

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
        try {
            postCoordinationService.createFirstImport(request.getDocumentId().id(), request.getProjectId(), executionContext.userId());
            return Mono.just(new UploadPostCoordinationResponse());
        } catch (Exception e) {
            LOGGER.error("Error uploading postcoordinations", e);
            throw new RuntimeException("Error uploading postcoordinations", e);
        }

    }
}
