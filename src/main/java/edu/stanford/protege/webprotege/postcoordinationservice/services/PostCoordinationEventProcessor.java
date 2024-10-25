package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.*;

@Service
public class PostCoordinationEventProcessor {


    private PostCoordinationSpecification findSpecificationWithLinearizationView(String linearizationView, HashSet<PostCoordinationSpecification> postCoordinationSpecification) {
        return postCoordinationSpecification.stream().filter(spec -> spec.getLinearizationView().equalsIgnoreCase(linearizationView))
                .findFirst()
                .orElse(new PostCoordinationSpecification(linearizationView, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

    }

    public WhoficCustomScalesValues processCustomScaleHistory(EntityCustomScalesValuesHistory entityCustomScalesValuesHistory) {
        WhoficCustomScalesValues response = new WhoficCustomScalesValues(entityCustomScalesValuesHistory.getWhoficEntityIri(), new ArrayList<>());
        for (PostCoordinationCustomScalesRevision revision : entityCustomScalesValuesHistory.getPostCoordinationCustomScalesRevisions()) {
            for (PostCoordinationCustomScalesValueEvent event : revision.postCoordinationEvents()) {
                event.applyEvent(response);
            }
        }
        List<PostCoordinationScaleCustomization> nonEmptyCustomizations = response.scaleCustomizations().stream()
                .filter(scale -> !scale.getPostcoordinationScaleValues().isEmpty())
                .toList();
        return new WhoficCustomScalesValues(entityCustomScalesValuesHistory.getWhoficEntityIri(), nonEmptyCustomizations);
    }

    public WhoficEntityPostCoordinationSpecification processHistory(@Nonnull EntityPostCoordinationHistory postCoordinationHistory) {

        var postCoordinationSpecification = new HashSet<PostCoordinationSpecification>();
        for (PostCoordinationSpecificationRevision revision : postCoordinationHistory.getPostCoordinationRevisions()) {
            for (PostCoordinationViewEvent viewEvent : revision.postCoordinationEvents()) {
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
