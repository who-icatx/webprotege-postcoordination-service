package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff;


import com.google.common.base.Preconditions;
import edu.stanford.protege.webprotege.diff.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationViewEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.services.LinearizationService;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.*;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class Revision2DiffElementsTranslator {

    private final ChangeOperationVisitorEx<DiffOperation> changeOperationVisitor;

    private final LinearizationService linearizationService;


    public Revision2DiffElementsTranslator(LinearizationService linearizationService) {
        this.linearizationService = linearizationService;
        this.changeOperationVisitor = new ChangeOperationVisitorEx<>() {
            @Override
            public DiffOperation visit(AddCustomScaleValueEvent addScaleValueEvent) {
                return DiffOperation.ADD;
            }

            @Override
            public DiffOperation visit(RemoveCustomScaleValueEvent removeScaleValueEvent) {
                return DiffOperation.REMOVE;
            }
        };
    }

    public List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> getDiffElementsFromCustomScaleRevision(Map<String, List<PostCoordinationCustomScalesValueEvent>> eventsByAxis,
                                                                                                                                       Map<String, Integer> orderedAxisMap,
                                                                                                                                       Map<String, String> entityIrisAndNames) {
        final List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> changeRecordElements = new ArrayList<>();

        eventsByAxis.forEach(
                (axis, eventsForAxis) ->
                        eventsForAxis.forEach(
                                event -> changeRecordElements.add(toElement(axis, event, orderedAxisMap, entityIrisAndNames))
                        )
        );
        return changeRecordElements;
    }

    private DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent> toElement(
            String axis,
            PostCoordinationCustomScalesValueEvent customScalesValueEvent,
            Map<String, Integer> orderedAxisMap,
            Map<String, String> entityIrisAndNames) {

        Preconditions.checkNotNull(axis, "Axis cannot be null");
        Preconditions.checkNotNull(customScalesValueEvent, "Custom scales value event cannot be null");

        CustomScaleDocumentChange sourceDocument;

        String axisName = entityIrisAndNames.getOrDefault(axis, axis);
        int sortingCode = orderedAxisMap.getOrDefault(axis, Integer.MAX_VALUE);

        sourceDocument = CustomScaleDocumentChange.create(axis, axisName, sortingCode);

        return new DiffElement<>(
                getDiffOperation(customScalesValueEvent),
                sourceDocument,
                customScalesValueEvent
        );
    }



    private DiffOperation getDiffOperation(PostCoordinationCustomScalesValueEvent event) {
        return event.accept(changeOperationVisitor);
    }


    public List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> getDiffElementsFromSpecRevision(List<PostCoordinationViewEvent> changesByView, Map<String, Integer> orderAxisMapWithSubAxis) {
        final List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> changeRecordElements = new ArrayList<>();

        List<LinearizationDefinition> linearizationDefinitions = linearizationService.getLinearizationDefinitions();
        changesByView.forEach((eventsInView) -> changeRecordElements.add(toElement(eventsInView.linearizationView(), linearizationDefinitions, eventsInView.axisEvents(), orderAxisMapWithSubAxis)));
        return changeRecordElements;
    }

    private DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>> toElement(String linearizationView,
                                                                                                List<LinearizationDefinition> linearizationDefinitions,
                                                                                                List<PostCoordinationSpecificationEvent> postSpecEvents,
                                                                                                Map<String, Integer> orderedAxisMap) {

        SpecDocumentChange sourceDocument;
        Optional<LinearizationDefinition> linearizationDefinitionOptional = linearizationDefinitions.stream()
                .filter(linearizationDefinition -> linearizationDefinition.getLinearizationUri().equals(linearizationView))
                .findFirst();
        if (linearizationDefinitionOptional.isPresent()) {
            LinearizationDefinition linDef = linearizationDefinitionOptional.get();
            sourceDocument = SpecDocumentChange.create(linearizationView, linDef.getDisplayLabel(), linDef.getLinearizationId(), linDef.getSortingCode());
        } else {
            sourceDocument = SpecDocumentChange.create(linearizationView, linearizationView, linearizationView, "0");
        }

        List<PostCoordinationSpecificationEvent> mutatedPostSpecEvents = postSpecEvents.stream()
                .sorted(Comparator.comparingInt(
                                event -> orderedAxisMap.getOrDefault(event.getPostCoordinationAxis(), Integer.MAX_VALUE)
                        )
                )
                .toList();
        return new DiffElement<>(
                getAddDiffOperation(),
                sourceDocument,
                mutatedPostSpecEvents
        );
    }

    private DiffOperation getAddDiffOperation() {
        return DiffOperation.ADD;
    }
}
