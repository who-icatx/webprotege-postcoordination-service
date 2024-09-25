package edu.stanford.protege.webprotege.postcoordinationservice.handlers;


import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityCustomScaleValueResponse;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetEntityCustomScaleValuesRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationSpecificationsRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.services.PostCoordinationEventProcessor;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;


@WebProtegeHandler
public class GetEntityCustomScaleValuesCommandHandler implements CommandHandler<GetEntityCustomScaleValuesRequest, GetEntityCustomScaleValueResponse> {


    private final PostCoordinationSpecificationsRepository repository;

    private final PostCoordinationEventProcessor postCoordinationEventProcessor;

    public GetEntityCustomScaleValuesCommandHandler(PostCoordinationSpecificationsRepository repository, PostCoordinationEventProcessor postCoordinationEventProcessor) {
        this.repository = repository;
        this.postCoordinationEventProcessor = postCoordinationEventProcessor;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return GetEntityCustomScaleValuesRequest.CHANNEL;
    }

    @Override
    public Class<GetEntityCustomScaleValuesRequest> getRequestClass() {
        return GetEntityCustomScaleValuesRequest.class;
    }

    @Override
    public Mono<GetEntityCustomScaleValueResponse> handleRequest(GetEntityCustomScaleValuesRequest request, ExecutionContext executionContext) {
        WhoficCustomScalesValues processedScales = postCoordinationEventProcessor.fetchCustomScalesHistory(request.entityIRI(), request.projectId());

        return Mono.just(new GetEntityCustomScaleValueResponse(request.entityIRI(), processedScales));
    }
}
