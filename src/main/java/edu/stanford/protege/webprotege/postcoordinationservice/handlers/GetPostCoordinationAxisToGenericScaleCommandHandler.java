package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostcoordinationAxisToGenericScale;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostcoordinationAxisToGenericScaleRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.List;

@WebProtegeHandler
public class GetPostCoordinationAxisToGenericScaleCommandHandler implements CommandHandler<GetPostcoordinationAxisToGenericScaleRequest, GetPostcoordinationAxisToGenericScaleResponse> {

    private final PostcoordinationAxisToGenericScaleRepository axisToGenericScaleRepository;

    public GetPostCoordinationAxisToGenericScaleCommandHandler(PostcoordinationAxisToGenericScaleRepository axisToGenericScaleRepository) {
        this.axisToGenericScaleRepository = axisToGenericScaleRepository;
    }

    @NonNull
    @Override
    public String getChannelName() {
        return GetPostcoordinationAxisToGenericScaleRequest.CHANNEL;
    }

    @Override
    public Class<GetPostcoordinationAxisToGenericScaleRequest> getRequestClass() {
        return GetPostcoordinationAxisToGenericScaleRequest.class;
    }

    @Override
    public Mono<GetPostcoordinationAxisToGenericScaleResponse> handleRequest(GetPostcoordinationAxisToGenericScaleRequest request, ExecutionContext executionContext) {
        List<PostcoordinationAxisToGenericScale> postcoordinationAxisToGenericScales = axisToGenericScaleRepository.getPostCoordAxisToGenericScale();

        return Mono.just(GetPostcoordinationAxisToGenericScaleResponse.create(postcoordinationAxisToGenericScales));
    }
}
