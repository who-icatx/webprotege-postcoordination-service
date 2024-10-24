package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationService;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class AddEntitySpecificationRevisionCommandHandler implements CommandHandler<AddEntitySpecificationRevisionRequest, AddEntitySpecificationRevisionResponse> {

    private final PostCoordinationService postCoordService;

    public AddEntitySpecificationRevisionCommandHandler(PostCoordinationService postCoordService) {
        this.postCoordService = postCoordService;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return AddEntitySpecificationRevisionRequest.CHANNEL;
    }

    @Override
    public Class<AddEntitySpecificationRevisionRequest> getRequestClass() {
        return AddEntitySpecificationRevisionRequest.class;
    }

    @Override
    public Mono<AddEntitySpecificationRevisionResponse> handleRequest(AddEntitySpecificationRevisionRequest request, ExecutionContext executionContext) {
        postCoordService.addSpecificationRevision(request.entitySpecification(), executionContext.userId(), request.projectId());
        return Mono.just(new AddEntitySpecificationRevisionResponse());
    }
}
