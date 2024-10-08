package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff;


import edu.stanford.protege.webprotege.diff.*;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableAxisLabel;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.CustomScaleDocumentChange;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class Revision2DiffElementsTranslator {

    private final ChangeOperationVisitorEx<DiffOperation> changeOperationVisitor;

    public Revision2DiffElementsTranslator() {
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

    public List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> getDiffElementsFromCustomScaleRevision(Map<String, List<PostCoordinationCustomScalesValueEvent>> eventsByViews,
                                                                                                                                       List<String> orderedAxisList,
                                                                                                                                       List<TableAxisLabel> tableAxisLabels) {
        final List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> changeRecordElements = new ArrayList<>();

        eventsByViews.forEach(
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
}
