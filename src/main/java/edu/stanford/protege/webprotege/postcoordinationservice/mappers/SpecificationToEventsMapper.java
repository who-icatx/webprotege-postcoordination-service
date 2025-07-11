package edu.stanford.protege.webprotege.postcoordinationservice.mappers;

import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;

import java.util.*;
import java.util.stream.Collectors;


public class SpecificationToEventsMapper {


    public static List<PostCoordinationSpecificationEvent> convertFromSpecification(PostCoordinationSpecification specification, Set<String> allowedAxis) {
        List<PostCoordinationSpecificationEvent> response = new ArrayList<>();

        response.addAll(specification.getAllowedAxes().stream().filter(allowedAxis::contains).map(axis -> new AddToAllowedAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getDefaultAxes().stream().filter(allowedAxis::contains).map(axis -> new AddToDefaultAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getRequiredAxes().stream().filter(allowedAxis::contains).map(axis -> new AddToRequiredAxisEvent(axis, specification.getLinearizationView())).toList());
        response.addAll(specification.getNotAllowedAxes().stream().filter(allowedAxis::contains).map(axis -> new AddToNotAllowedAxisEvent(axis, specification.getLinearizationView())).toList());

        return response;
    }

    public static Set<PostCoordinationCustomScalesValueEvent> convertToFirstImportEvents(WhoficCustomScalesValues whoficCustomScalesValues) {
        Set<PostCoordinationCustomScalesValueEvent> response = new HashSet<>();

        for (PostCoordinationScaleCustomization request : whoficCustomScalesValues.scaleCustomizations()) {
            response.addAll(request.getPostcoordinationScaleValues()
                    .stream()
                    .map(scaleValue -> new AddCustomScaleValueEvent(request.getPostcoordinationAxis(), scaleValue)).toList());
        }

        return response;
    }


    public static Set<PostCoordinationViewEvent> createEventsFromDiff(WhoficEntityPostCoordinationSpecification existingSpecification, WhoficEntityPostCoordinationSpecification newSpecification) {

        Set<PostCoordinationViewEvent> response = new HashSet<>();

        for (PostCoordinationSpecification spec : newSpecification.postcoordinationSpecifications()) {
            List<PostCoordinationSpecificationEvent> events = new ArrayList<>();
            Optional<PostCoordinationSpecification> oldSpec = existingSpecification.postcoordinationSpecifications().stream()
                    .filter(s -> s.getLinearizationView().equalsIgnoreCase(spec.getLinearizationView()))
                    .findFirst();

            if (oldSpec.isPresent()) {

                List<String> newAllowedAxis = new ArrayList<>(spec.getAllowedAxes());
                newAllowedAxis.removeAll(oldSpec.get().getAllowedAxes());
                events.addAll(newAllowedAxis.stream().map(axis -> new AddToAllowedAxisEvent(axis, spec.getLinearizationView())).toList());

                List<String> newNotAllowedAxis = new ArrayList<>(spec.getNotAllowedAxes());
                newNotAllowedAxis.removeAll(oldSpec.get().getNotAllowedAxes());
                events.addAll(newNotAllowedAxis.stream().map(axis -> new AddToNotAllowedAxisEvent(axis, spec.getLinearizationView())).toList());

                List<String> newDefaultAxis = new ArrayList<>(spec.getDefaultAxes());
                newDefaultAxis.removeAll(oldSpec.get().getDefaultAxes());
                events.addAll(newDefaultAxis.stream().map(axis -> new AddToDefaultAxisEvent(axis, spec.getLinearizationView())).toList());

                List<String> newRequiredAxis = new ArrayList<>(spec.getRequiredAxes());
                newRequiredAxis.removeAll(oldSpec.get().getRequiredAxes());
                events.addAll(newRequiredAxis.stream().map(axis -> new AddToRequiredAxisEvent(axis, spec.getLinearizationView())).toList());
            } else {
                events.addAll(spec.getAllowedAxes().stream().map(axis -> new AddToAllowedAxisEvent(axis, spec.getLinearizationView())).toList());
                events.addAll(spec.getNotAllowedAxes().stream().map(axis -> new AddToNotAllowedAxisEvent(axis, spec.getLinearizationView())).toList());
                events.addAll(spec.getRequiredAxes().stream().map(axis -> new AddToRequiredAxisEvent(axis, spec.getLinearizationView())).toList());
                events.addAll(spec.getDefaultAxes().stream().map(axis -> new AddToDefaultAxisEvent(axis, spec.getLinearizationView())).toList());
            }

            if (!events.isEmpty()) {
                response.add(new PostCoordinationViewEvent(spec.getLinearizationView(), events));
            }
        }


        return response;
    }

    public static Set<PostCoordinationCustomScalesValueEvent> createScaleEventsFromDiff(WhoficCustomScalesValues oldScales, WhoficCustomScalesValues newScales) {
        Set<PostCoordinationCustomScalesValueEvent> events = new HashSet<>();

        for (PostCoordinationScaleCustomization scalesCustomization : newScales.scaleCustomizations()) {

            PostCoordinationScaleCustomization oldScaleCustomization = oldScales.scaleCustomizations().stream().filter(scale -> scale.getPostcoordinationAxis().equalsIgnoreCase(scalesCustomization.getPostcoordinationAxis()))
                    .findFirst().orElse(new PostCoordinationScaleCustomization(new ArrayList<>(), scalesCustomization.getPostcoordinationAxis()));

            List<String> addScales = new ArrayList<>(scalesCustomization.getPostcoordinationScaleValues());
            addScales.removeAll(oldScaleCustomization.getPostcoordinationScaleValues());

            events.addAll(addScales.stream().map(scale -> new AddCustomScaleValueEvent(scalesCustomization.getPostcoordinationAxis(), scale)).toList());


            List<String> removeScales = new ArrayList<>(oldScaleCustomization.getPostcoordinationScaleValues());
            removeScales.removeAll(scalesCustomization.getPostcoordinationScaleValues());
            events.addAll(removeScales.stream().map(scale -> new RemoveCustomScaleValueEvent(scalesCustomization.getPostcoordinationAxis(), scale)).toList());

        }

        for (PostCoordinationScaleCustomization scalesCustomization : oldScales.scaleCustomizations()) {
            Optional<PostCoordinationScaleCustomization> newScaleCustomization = newScales.scaleCustomizations().stream().filter(scale -> scale.getPostcoordinationAxis().equalsIgnoreCase(scalesCustomization.getPostcoordinationAxis()))
                    .findFirst();
            if (newScaleCustomization.isEmpty()) {
                events.addAll(scalesCustomization.getPostcoordinationScaleValues().stream().map(scale -> new RemoveCustomScaleValueEvent(scalesCustomization.getPostcoordinationAxis(), scale)).toList());
            }
        }


        return events;
    }

    public static Map<String, List<PostCoordinationCustomScalesValueEvent>> groupScaleEventsByAxis(List<PostCoordinationCustomScalesValueEvent> events) {
        return events.stream().collect(Collectors.groupingBy(PostCoordinationCustomScalesValueEvent::getPostCoordinationAxis));
    }
}
