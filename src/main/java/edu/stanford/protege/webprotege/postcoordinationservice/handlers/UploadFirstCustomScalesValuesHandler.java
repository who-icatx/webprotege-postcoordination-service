package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import static edu.stanford.protege.webprotege.postcoordinationservice.handlers.UploadFirstCustomScalesValuesRequest.CHANNEL;

@WebProtegeHandler
public class UploadFirstCustomScalesValuesHandler implements CommandHandler<UploadFirstCustomScalesValuesRequest, UploadFirstCustomScalesValuesResponse> {


    private final PostCoordinationService postCoordinationService;

    public UploadFirstCustomScalesValuesHandler(PostCoordinationService postCoordinationService) {
        this.postCoordinationService = postCoordinationService;
    }

    @NonNull
    @Override
    public String getChannelName() {
        return CHANNEL;
    }

    @Override
    public Class<UploadFirstCustomScalesValuesRequest> getRequestClass() {
        return UploadFirstCustomScalesValuesRequest.class;
    }

    @Override
    public Mono<UploadFirstCustomScalesValuesResponse> handleRequest(UploadFirstCustomScalesValuesRequest request, ExecutionContext executionContext) {
        postCoordinationService.crateFirstCustomScalesValuesImport(request.documentId().id(), request.projectId(), executionContext.userId());
        return Mono.just(new UploadFirstCustomScalesValuesResponse());
    }
}
