package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff;

import edu.stanford.protege.webprotege.diff.DiffElement;
import edu.stanford.protege.webprotege.entity.EntityNode;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.*;

import java.io.Serializable;
import java.util.*;

public class DiffElementRenderer<S extends Serializable> {

    private final CustomScaleChangeVisitor<String> visitor;

    List<EntityNode> renderedEntities;

    public DiffElementRenderer(List<EntityNode> renderedEntities) {
        this.renderedEntities = renderedEntities;
        visitor = new CustomScaleChangeVisitor<>() {


            @Override
            public String visit(AddCustomScaleValueEvent addScaleValueEvent) {
                return renderHtmlForScaleValue(addScaleValueEvent, true);
            }

            @Override
            public String visit(RemoveCustomScaleValueEvent removeScaleValueEvent) {
                return renderHtmlForScaleValue(removeScaleValueEvent, false);
            }

            @Override
            public String getDefaultReturnValue() {
                throw new RuntimeException();
            }
        };
    }


    public DiffElement<String, String> render(DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent> element) {
        var eventsByAxis = element.getLineElement();
        var renderedLine = renderLine(eventsByAxis);
        var source = element.getSourceDocument();
        var renderedSource = renderSource(source);
        renderedLine = (renderedLine == null || renderedLine.isEmpty()) ? "no value" : renderedLine;
        return new DiffElement<>(
                element.getDiffOperation(),
                renderedSource,
                renderedLine
        );
    }

    private String renderSource(CustomScaleDocumentChange source) {
        final StringBuilder stringBuilder = new StringBuilder();

        var displayLabel = source.getPostCoordinationName() != null ? source.getPostCoordinationName() : source.getPostCoordinationAxis();

        stringBuilder.append("<span class=\"ms-quantifier-kw\">");
        stringBuilder.append(displayLabel);
        stringBuilder.append("</span>");

        return stringBuilder.toString();
    }

    public String renderLine(PostCoordinationCustomScalesValueEvent change) {
        return change.accept(visitor);
    }

    private String renderHtmlForScaleValue(PostCoordinationCustomScalesValueEvent scaleValueEvent, boolean addValue) {
        Optional<EntityNode> axisNameOptional = renderedEntities.stream()
                .filter(renderedEntity -> renderedEntity.getEntity().toStringID().equals(scaleValueEvent.getPostCoordinationAxis()))
                .findFirst();
        String axisName = scaleValueEvent.getPostCoordinationAxis();
        if (axisNameOptional.isPresent()) {
            axisName = axisNameOptional.get().getBrowserText();
        }
        Optional<EntityNode> scaleValueSelectionNameOptional = renderedEntities.stream()
                .filter(renderedEntity -> renderedEntity.getEntity().toStringID().equals(scaleValueEvent.getPostCoordinationScaleValue()))
                .findFirst();
        String scaleValueSelectionName = scaleValueEvent.getPostCoordinationScaleValue();
        if (scaleValueSelectionNameOptional.isPresent()) {
            scaleValueSelectionName = scaleValueSelectionNameOptional.get().getBrowserText();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&nbsp;<span>");
        if (addValue) {
            stringBuilder.append("Added for axis");
        } else {
            stringBuilder.append("Removed for axis");
        }
        stringBuilder.append(axisName);
        stringBuilder.append(" value of ");
        stringBuilder.append("<span class=\"ms-literal\">\"");
        stringBuilder.append(scaleValueSelectionName);
        stringBuilder.append("\"</span>");
        stringBuilder.append("</span>;&nbsp;");

        return stringBuilder.toString();
    }
}
