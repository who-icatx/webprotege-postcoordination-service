package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class AddEntityCustomScalesRevisionCommandHandler implements CommandHandler<AddEntityCustomScalesRevisionRequest, AddEntityCustomScalesRevisionResponse> {

    private final PostCoordinationService postCoordService;

    public AddEntityCustomScalesRevisionCommandHandler(PostCoordinationService postCoordService) {
        this.postCoordService = postCoordService;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return AddEntityCustomScalesRevisionRequest.CHANNEL;
    }

    @Override
    public Class<AddEntityCustomScalesRevisionRequest> getRequestClass() {
        return AddEntityCustomScalesRevisionRequest.class;
    }

    @Override
    public Mono<AddEntityCustomScalesRevisionResponse> handleRequest(AddEntityCustomScalesRevisionRequest request, ExecutionContext executionContext) {

        postCoordService.addCustomScaleRevision(request.entityCustomScaleValues(), request.projectId(), executionContext.userId());

        return Mono.just(new AddEntityCustomScalesRevisionResponse());
    }
}
