package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.*;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;

@WebProtegeHandler
public class GetPostCoordinationAxisToGenericScaleCommandHandler implements CommandHandler<GetPostCoordinationAxisToGenericScaleRequest, GetPostCoordinationAxisToGenericScaleResponse> {

    private final PostcoordinationAxisToGenericScaleRepository axisToGenericScaleRepository;

    public GetPostCoordinationAxisToGenericScaleCommandHandler(PostcoordinationAxisToGenericScaleRepository axisToGenericScaleRepository) {
        this.axisToGenericScaleRepository = axisToGenericScaleRepository;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return GetPostCoordinationAxisToGenericScaleRequest.CHANNEL;
    }

    @Override
    public Class<GetPostCoordinationAxisToGenericScaleRequest> getRequestClass() {
        return GetPostCoordinationAxisToGenericScaleRequest.class;
    }

    @Override
    public Mono<GetPostCoordinationAxisToGenericScaleResponse> handleRequest(GetPostCoordinationAxisToGenericScaleRequest request, ExecutionContext executionContext) {
        List<AxisToGenericScale> axisToGenericScales = axisToGenericScaleRepository.getPostCoordAxisToGenericScale();

        return Mono.just(GetPostCoordinationAxisToGenericScaleResponse.create(axisToGenericScales));
    }
}
