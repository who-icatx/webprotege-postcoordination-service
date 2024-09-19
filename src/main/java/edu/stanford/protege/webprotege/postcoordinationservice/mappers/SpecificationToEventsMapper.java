package edu.stanford.protege.webprotege.postcoordinationservice.mappers;

import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationCustomScalesRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SpecificationToEventsMapper {


    public static List<PostCoordinationSpecificationEvent> convertFromSpecification(PostCoordinationSpecification specification) {
        List<PostCoordinationSpecificationEvent> response = new ArrayList<>();

        response.addAll(specification.getAllowedAxes().stream().map(axis -> new AddToAllowedAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getDefaultAxes().stream().map(axis -> new AddToDefaultAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getRequiredAxes().stream().map(axis -> new AddToRequiredAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getNotAllowedAxes().stream().map(axis -> new AddToNotAllowedAxisEvent(axis, specification.getLinearizationView())).toList());

        return response;
    }

    public static Set<PostCoordinationCustomScalesValueEvent> convertToFirstImportEvents(WhoficCustomScalesValues whoficCustomScalesValues) {
        Set<PostCoordinationCustomScalesValueEvent> response = new HashSet<>();

        for(PostCoordinationCustomScalesRequest request : whoficCustomScalesValues.scaleCustomizations()) {
            response.addAll(request.getPostCoordinationScalesValues()
                    .stream()
                    .map(scaleValue -> new AddCustomScaleValueEvent(request.getPostCoordinationAxis(), scaleValue)).toList());
        }

        return response;
    }

}
