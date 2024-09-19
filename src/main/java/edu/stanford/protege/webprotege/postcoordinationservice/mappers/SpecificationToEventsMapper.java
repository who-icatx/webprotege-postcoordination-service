package edu.stanford.protege.webprotege.postcoordinationservice.mappers;

import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;

import java.util.ArrayList;
import java.util.List;


public class SpecificationToEventsMapper {


    public static List<PostCoordinationEvent> convertFromSpecification(PostCoordinationSpecification specification) {
        List<PostCoordinationEvent> response = new ArrayList<>();

        response.addAll(specification.getAllowedAxes().stream().map(axis -> new AddToAllowedAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getDefaultAxes().stream().map(axis -> new AddToDefaultAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getRequiredAxes().stream().map(axis -> new AddToRequiredAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getNotAllowedAxes().stream().map(axis -> new AddToNotAllowedAxisEvent(axis, specification.getLinearizationView())).toList());

        return response;
    }

}
