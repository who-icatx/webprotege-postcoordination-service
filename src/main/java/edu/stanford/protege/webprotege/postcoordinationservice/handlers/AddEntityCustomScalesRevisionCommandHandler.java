package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

@WebProtegeHandler
public class AddEntityCustomScalesRevisionCommandHandler implements CommandHandler<AddEntityCustomScalesRevisionRequest, AddEntityCustomScalesRevisionResponse> {

    private final PostCoordinationEventProcessor eventProcessor;

    public AddEntityCustomScalesRevisionCommandHandler(PostCoordinationEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return AddEntitySpecificationRevisionRequest.CHANNEL;
    }

    @Override
    public Class<AddEntityCustomScalesRevisionRequest> getRequestClass() {
        return AddEntityCustomScalesRevisionRequest.class;
    }

    @Override
    public Mono<AddEntityCustomScalesRevisionResponse> handleRequest(AddEntityCustomScalesRevisionRequest request, ExecutionContext executionContext) {

        eventProcessor.saveNewCustomScalesRevision(request.customScalesValues(), executionContext.userId().id(), request.projectId());

        return Mono.just(new AddEntityCustomScalesRevisionResponse());
    }
}
