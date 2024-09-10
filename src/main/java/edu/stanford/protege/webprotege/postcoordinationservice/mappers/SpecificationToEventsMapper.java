package edu.stanford.protege.webprotege.postcoordinationservice.mappers;

import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationRevision;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecificationRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class SpecificationToEventsMapper {


    public static List<PostCoordinationEvent> convertFromSpecification(PostCoordinationSpecificationRequest specification) {
        List<PostCoordinationEvent> response = new ArrayList<>();

        response.addAll(specification.getAllowedAxes().stream().map(axis -> new AddToAllowedAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getDefaultAxes().stream().map(axis -> new AddToDefaultAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getRequiredAxes().stream().map(axis -> new AddToRequiredAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getNotAllowedAxes().stream().map(axis -> new AddToNotAllowedAxisEvent(axis, specification.getLinearizationView())).toList());

        return response;
    }

    public static PostCoordinationRevision mapToRevision(ProjectId projectId, String userId, WhoficEntityPostCoordinationSpecification specification) {
        Set<PostCoordinationEvent> events = specification.getPostCoordinationSpecifications().stream()
                .flatMap(spec -> SpecificationToEventsMapper.convertFromSpecification(spec).stream()).collect(Collectors.toSet());
        return new PostCoordinationRevision(userId, new Date().getTime(), events);
    }
}
