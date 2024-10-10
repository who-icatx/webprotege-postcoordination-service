package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class AddEntitySpecificationRevisionCommandHandler implements CommandHandler<AddEntitySpecificationRevisionRequest, AddEntitySpecificationRevisionResponse> {

    private final PostCoordinationEventProcessor eventProcessor;

    public AddEntitySpecificationRevisionCommandHandler(PostCoordinationEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
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
        eventProcessor.saveNewSpecificationRevision(request.entitySpecification(), executionContext.userId(), request.projectId());
        return Mono.just(new AddEntitySpecificationRevisionResponse());
    }
}
