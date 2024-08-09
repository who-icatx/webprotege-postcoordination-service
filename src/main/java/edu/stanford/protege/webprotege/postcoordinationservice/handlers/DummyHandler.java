package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;


@WebProtegeHandler
public class DummyHandler implements CommandHandler<DummyHandlerRequest, DummyHandlerResponse> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DummyHandler.class);



    @NotNull
    @Override
    public String getChannelName() {
        return DummyHandlerRequest.CHANNEL;
    }

    @Override
    public Class<DummyHandlerRequest> getRequestClass() {
        return DummyHandlerRequest.class;
    }

    @Override
    public Mono<DummyHandlerResponse> handleRequest(DummyHandlerRequest request, ExecutionContext executionContext) {
        LOGGER.info("Logging a dummy request " + request);
        return Mono.empty();
    }
}
