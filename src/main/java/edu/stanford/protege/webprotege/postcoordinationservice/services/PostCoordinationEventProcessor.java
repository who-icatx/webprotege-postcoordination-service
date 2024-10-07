package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.mappers.SpecificationToEventsMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationSpecificationsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.*;

@Service
public class PostCoordinationEventProcessor {

    private final PostCoordinationSpecificationsRepository repository;

    public PostCoordinationEventProcessor(PostCoordinationSpecificationsRepository repository) {
        this.repository = repository;
    }

    public void saveNewSpecificationRevision(WhoficEntityPostCoordinationSpecification newSpecification, String userId, ProjectId projectId) {
        WhoficEntityPostCoordinationSpecification existingSpecification = fetchHistory(newSpecification.whoficEntityIri(), projectId);
        Set<PostCoordinationViewEvent> events = SpecificationToEventsMapper.createEventsFromDiff(existingSpecification, newSpecification);

        PostCoordinationSpecificationRevision revision = new PostCoordinationSpecificationRevision(userId, new Date().getTime(), events);


        repository.addSpecificationRevision(newSpecification.whoficEntityIri(), projectId, revision);
    }

    public void saveNewCustomScalesRevision(WhoficCustomScalesValues newScales, String userId, ProjectId projectId) {
        WhoficCustomScalesValues oldScales = fetchCustomScalesHistory(newScales.whoficEntityIri(), projectId);

        Set<PostCoordinationCustomScalesValueEvent> events = SpecificationToEventsMapper.createScaleEventsFromDiff(oldScales, newScales);

        if (events.isEmpty()) {
            PostCoordinationCustomScalesRevision revision = new PostCoordinationCustomScalesRevision(userId, new Date().getTime(), events);

            repository.addCustomScalesRevision(newScales.whoficEntityIri(), projectId, revision);
        }
    }

    public WhoficEntityPostCoordinationSpecification fetchHistory(String entityIri, ProjectId projectId) {
        return this.repository.getExistingHistoryOrderedByRevision(entityIri, projectId)
                .map(this::processHistory)
                .orElseGet(() -> new WhoficEntityPostCoordinationSpecification(entityIri, null, Collections.emptyList()));
    }

    private PostCoordinationSpecification findSpecificationWithLinearizationView(String linearizationView, HashSet<PostCoordinationSpecification> postCoordinationSpecification) {
        return postCoordinationSpecification.stream().filter(spec -> spec.getLinearizationView().equalsIgnoreCase(linearizationView))
                .findFirst()
                .orElse(new PostCoordinationSpecification(linearizationView, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

    }

    public WhoficCustomScalesValues fetchCustomScalesHistory(String entityIri, ProjectId projectId) {
        return this.repository.getExistingCustomScaleHistoryOrderedByRevision(entityIri, projectId)
                .map(this::processCustomScaleHistory)
                .orElseGet(() -> new WhoficCustomScalesValues(entityIri, Collections.emptyList()));

    }

    private WhoficCustomScalesValues processCustomScaleHistory(EntityCustomScalesValuesHistory entityCustomScalesValuesHistory) {
        WhoficCustomScalesValues response = new WhoficCustomScalesValues(entityCustomScalesValuesHistory.getWhoficEntityIri(), new ArrayList<>());
        for (PostCoordinationCustomScalesRevision revision : entityCustomScalesValuesHistory.getPostCoordinationCustomScalesRevisions()) {
            for (PostCoordinationCustomScalesValueEvent event : revision.postCoordinationEventList()) {
                event.applyEvent(response);
            }
        }
        List<PostCoordinationScaleCustomization> nonEmptyCustomizations = response.scaleCustomizations().stream()
                .filter(scale -> !scale.getPostcoordinationScaleValues().isEmpty())
                .toList();
        return new WhoficCustomScalesValues(entityCustomScalesValuesHistory.getWhoficEntityIri(), nonEmptyCustomizations);
    }

    private WhoficEntityPostCoordinationSpecification processHistory(@Nonnull EntityPostCoordinationHistory postCoordinationHistory) {

        var postCoordinationSpecification = new HashSet<PostCoordinationSpecification>();
        for (PostCoordinationSpecificationRevision revision : postCoordinationHistory.getPostCoordinationRevisions()) {
            for (PostCoordinationViewEvent viewEvent : revision.postCoordinationEventList()) {
                PostCoordinationSpecification specification = findSpecificationWithLinearizationView(viewEvent.linearizationView(), postCoordinationSpecification);
                for (PostCoordinationSpecificationEvent event : viewEvent.axisEvents()) {
                    event.applyEvent(specification);
                }
                postCoordinationSpecification.add(specification);
            }
        }

        return new WhoficEntityPostCoordinationSpecification(postCoordinationHistory.getWhoficEntityIri(), "ICD", postCoordinationSpecification.stream().toList());
    }

}
