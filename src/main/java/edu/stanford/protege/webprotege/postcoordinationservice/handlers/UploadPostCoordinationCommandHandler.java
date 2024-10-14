package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.slf4j.*;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;


@WebProtegeHandler
public class UploadPostCoordinationCommandHandler implements CommandHandler<UploadPostCoordinationRequest, UploadPostCoordinationResponse> {

    private final static Logger LOGGER = LoggerFactory.getLogger(UploadPostCoordinationCommandHandler.class);

    private final PostCoordinationService postCoordinationService;

    public UploadPostCoordinationCommandHandler(PostCoordinationService postCoordinationService) {
        this.postCoordinationService = postCoordinationService;
    }

    @NonNull
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
            postCoordinationService.createFirstSpecificationImport(request.getDocumentId().id(), request.getProjectId(), executionContext.userId());
            return Mono.just(new UploadPostCoordinationResponse());
        } catch (Exception e) {
            LOGGER.error("Error uploading postcoordinations", e);
            throw new RuntimeException("Error uploading postcoordinations", e);
        }

    }
}
