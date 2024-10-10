package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff;


import edu.stanford.protege.webprotege.diff.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
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
                                                                                                                                       List<String> orderedAxisList,
                                                                                                                                       List<TableAxisLabel> tableAxisLabels) {
        final List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> changeRecordElements = new ArrayList<>();

        eventsByAxis.forEach(
                (axis, eventsForAxis) ->
                        eventsForAxis.forEach(
                                event -> changeRecordElements.add(toElement(axis, event, orderedAxisList, tableAxisLabels))
                        )
        );
        return changeRecordElements;
    }

    private DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent> toElement(String axis,
                                                                                                     PostCoordinationCustomScalesValueEvent customScalesValueEvent,
                                                                                                     List<String> orderedAxisList,
                                                                                                     List<TableAxisLabel> tableAxisLabels) {
        CustomScaleDocumentChange sourceDocument;
        Optional<TableAxisLabel> axisLabelOptional = tableAxisLabels.stream()
                .filter(tableAxisLabel -> tableAxisLabel.getPostCoordinationAxis().equals(axis))
                .findFirst();
        if (axisLabelOptional.isPresent()) {
            TableAxisLabel axisLabel = axisLabelOptional.get();
            sourceDocument = CustomScaleDocumentChange.create(axisLabel.getPostCoordinationAxis(), axisLabel.getScaleLabel(), orderedAxisList.indexOf(axis));
        } else {
            sourceDocument = CustomScaleDocumentChange.create(axis, axis, orderedAxisList.indexOf(axis));
        }
        return new DiffElement<>(
                getDiffOperation(customScalesValueEvent),
                sourceDocument,
                customScalesValueEvent
        );
    }


    private DiffOperation getDiffOperation(PostCoordinationCustomScalesValueEvent event) {
        return event.accept(changeOperationVisitor);
    }


    public List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> getDiffElementsFromSpecRevision(List<PostCoordinationViewEvent> changesByView, List<String> orderAxisListWithSubAxis) {
        final List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> changeRecordElements = new ArrayList<>();

        changesByView.forEach((eventsInView) -> changeRecordElements.add(toElement(eventsInView.linearizationView(), eventsInView.axisEvents(), orderAxisListWithSubAxis)));
        return changeRecordElements;
    }

    private DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>> toElement(String linearizationView,
                                                                                                List<PostCoordinationSpecificationEvent> postSpecEvents,
                                                                                                List<String> orderedAxisList) {
        List<LinearizationDefinition> linearizationDefinitions = linearizationService.getLinearizationDefinitions();

        SpecDocumentChange sourceDocument;
        Optional<LinearizationDefinition> linearizationDefinitionOptional = linearizationDefinitions.stream()
                .filter(linearizationDefinition -> linearizationDefinition.getWhoficEntityIri().equals(linearizationView))
                .findFirst();
        if (linearizationDefinitionOptional.isPresent()) {
            LinearizationDefinition linDef = linearizationDefinitionOptional.get();
            sourceDocument = SpecDocumentChange.create(linearizationView, linDef.getDisplayLabel(), linDef.getId(), linDef.getSortingCode());
        } else {
            sourceDocument = SpecDocumentChange.create(linearizationView, linearizationView, linearizationView, "0");
        }

        List<PostCoordinationSpecificationEvent> mutatedPostSpecEvents = postSpecEvents.stream()
                .sorted(Comparator.comparingInt(event -> {
                                    int index = orderedAxisList.indexOf(event.getPostCoordinationAxis());
                                    return index == -1 ? Integer.MAX_VALUE : index;
                                }
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
