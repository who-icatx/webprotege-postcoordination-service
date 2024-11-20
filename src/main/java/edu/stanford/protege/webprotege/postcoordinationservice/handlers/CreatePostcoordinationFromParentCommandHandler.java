package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import edu.stanford.protege.webprotege.ipc.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.services.*;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.*;

@WebProtegeHandler
public class CreatePostcoordinationFromParentCommandHandler implements CommandHandler<CreatePostcoordinationFromParentRequest, CreatePostcoordinationFromParentResponse> {

    private final PostCoordinationService postCoordService;
    private final PostCoordinationRepository repo;
    private final PostCoordinationEventProcessor eventProcessor;
    private final LinearizationService linService;

    public CreatePostcoordinationFromParentCommandHandler(PostCoordinationService postCoordService,
                                                          PostCoordinationRepository repo,
                                                          PostCoordinationEventProcessor eventProcessor,
                                                          LinearizationService linService) {

        this.postCoordService = postCoordService;
        this.repo = repo;
        this.eventProcessor = eventProcessor;
        this.linService = linService;
    }

    @NotNull
    @Override
    public String getChannelName() {
        return CreatePostcoordinationFromParentRequest.CHANNEL;
    }

    @Override
    public Class<CreatePostcoordinationFromParentRequest> getRequestClass() {
        return CreatePostcoordinationFromParentRequest.class;
    }

    @Override
    public Mono<CreatePostcoordinationFromParentResponse> handleRequest(CreatePostcoordinationFromParentRequest request, ExecutionContext executionContext) {
        List<LinearizationDefinition> definitionList = linService.getLinearizationDefinitions();

        var parentWhoficHistoryOptional = repo.getExistingHistoryOrderedByRevision(request.parentEntityIri().toString(), request.projectId());
        parentWhoficHistoryOptional.ifPresent(parentWhoficHistory ->{
            List<PostCoordinationSpecification> newSpecsList = new ArrayList<>();

            var parentWhoficSpec = eventProcessor.processHistory(parentWhoficHistory);

            parentWhoficSpec.postcoordinationSpecifications().forEach(spec -> {
                var currDef = definitionList.stream().filter(lin -> lin.getWhoficEntityIri().equalsIgnoreCase(spec.getLinearizationView())).findFirst();
                if (currDef.isEmpty()) {
                    return;
                }
                var allAxes = new ArrayList<>(spec.getAllowedAxes());
                allAxes.addAll(spec.getDefaultAxes());
                allAxes.addAll(spec.getRequiredAxes());
                allAxes.addAll(spec.getNotAllowedAxes());
                PostCoordinationSpecification newSpec = new PostCoordinationSpecification(spec.getLinearizationView(), null, null, null, null);

                if (currDef.get().getCoreLinId() != null) {
                    newSpec.getDefaultAxes().addAll(allAxes);
                } else {
                    newSpec.getNotAllowedAxes().addAll(allAxes);
                }
                newSpecsList.add(newSpec);
            });

            postCoordService.addSpecificationRevision(
                    WhoficEntityPostCoordinationSpecification.create(request.newEntityIri().toString(), parentWhoficSpec.entityType(), newSpecsList),
                    executionContext.userId(),
                    request.projectId()
            );
        });

        return Mono.just(CreatePostcoordinationFromParentResponse.create());
    }
}
