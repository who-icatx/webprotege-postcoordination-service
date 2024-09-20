package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationSpecificationEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;

@Service
public class PostCoordinationEventProcessor {

    public WhoficEntityPostCoordinationSpecification processHistory(@Nonnull EntityPostCoordinationHistory postCoordinationHistory) {

        var postCoordinationSpecification = new HashSet<PostCoordinationSpecification>();
        for(PostCoordinationRevision revision: postCoordinationHistory.getPostCoordinationRevisions()) {
            for(PostCoordinationViewEvent viewEvent: revision.postCoordinationEventList()) {
                PostCoordinationSpecification specification = findSpecificationWithLinearizationView(viewEvent.linearizationView(), postCoordinationSpecification);
                for(PostCoordinationSpecificationEvent event : viewEvent.axisEvents()) {
                    event.applyEvent(specification);
                }
                postCoordinationSpecification.add(specification);
            }
        }

        return new WhoficEntityPostCoordinationSpecification(postCoordinationHistory.getWhoficEntityIri(), "ICD", postCoordinationSpecification.stream().toList());
    }

    private PostCoordinationSpecification findSpecificationWithLinearizationView(String linearizationView,  HashSet<PostCoordinationSpecification> postCoordinationSpecification) {
        return postCoordinationSpecification.stream().filter(spec -> spec.getLinearizationView().equalsIgnoreCase(linearizationView))
                .findFirst()
                .orElse(new PostCoordinationSpecification(linearizationView, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

    }


    public WhoficCustomScalesValues processCustomScaleHistory(EntityCustomScalesValuesHistory entityCustomScalesValuesHistory) {
        WhoficCustomScalesValues response = new WhoficCustomScalesValues(entityCustomScalesValuesHistory.getWhoficEntityIri(), new ArrayList<>());
        for(PostCoordinationCustomScalesRevision revision: entityCustomScalesValuesHistory.getPostCoordinationCustomScalesRevisions()) {
            for(PostCoordinationCustomScalesValueEvent event: revision.postCoordinationEventList()) {
                event.applyEvent(response);
            }
        }
        return response;
    }
}
