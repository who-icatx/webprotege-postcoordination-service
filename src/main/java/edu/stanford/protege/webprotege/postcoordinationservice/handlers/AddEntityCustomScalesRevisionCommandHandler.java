package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;


@WebProtegeHandler
public class AddEntityCustomScalesRevisionCommandHandler implements CommandHandler<AddEntityCustomScalesRevisionRequest, AddEntityCustomScalesRevisionResponse> {

    private final PostCoordinationEventProcessor eventProcessor;

    public AddEntityCustomScalesRevisionCommandHandler(PostCoordinationEventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @NonNull
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

        eventProcessor.saveNewCustomScalesRevision(request.entityCustomScaleValues(), executionContext.userId(), request.projectId());

        return Mono.just(new AddEntityCustomScalesRevisionResponse());
    }
}
