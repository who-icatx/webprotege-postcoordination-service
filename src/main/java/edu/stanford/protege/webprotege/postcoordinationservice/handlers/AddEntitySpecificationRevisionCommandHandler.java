package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class AddEntitySpecificationRevisionCommandHandler implements CommandHandler<AddEntitySpecificationRevisionRequest, AddEntitySpecificationRevisionResponse> {

    private final PostCoordinationEventProcessor eventProcessor;

    public AddEntitySpecificationRevisionCommandHandler(PostCoordinationEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @NonNull
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
