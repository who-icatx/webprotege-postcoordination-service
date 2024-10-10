package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff;

import edu.stanford.protege.webprotege.diff.DiffElement;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.*;

import java.io.Serializable;
import java.util.*;

public class DiffElementRenderer<S extends Serializable> {

    private final CustomScaleChangeVisitor<String> visitor;

    Map<String, String> entityIrisAndNames;

    public DiffElementRenderer(Map<String, String> entityIrisAndNames) {
        this.entityIrisAndNames = entityIrisAndNames;
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
        String axisName = entityIrisAndNames.get(scaleValueEvent.getPostCoordinationAxis());
        String scaleValueSelectionName = entityIrisAndNames.get(scaleValueEvent.getPostCoordinationScaleValue());

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


    public DiffElement<String, String> renderSpec(DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>> element) {
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

    public String renderLine(List<PostCoordinationSpecificationEvent> changeList) {
        StringBuilder stringBuilder = new StringBuilder();
        changeList.forEach(event -> {
            String axisName = entityIrisAndNames.get(event.getPostCoordinationAxis());

            stringBuilder.append("&nbsp;<span>");
            stringBuilder.append("Added for axis");
            stringBuilder.append(axisName);
            stringBuilder.append(" value of ");
            stringBuilder.append("<span class=\"ms-literal\">\"");
            stringBuilder.append(event.getUiDisplayName());
            stringBuilder.append("\"</span>");
            stringBuilder.append("</span>;&nbsp;");
        });


        return stringBuilder.toString();
    }

    private String renderSource(SpecDocumentChange source) {
        final StringBuilder stringBuilder = new StringBuilder();

        var displayLabel = source.getLinearizationViewName() != null ? source.getLinearizationViewName() : source.getLinearizationViewIri();

        stringBuilder.append("<span class=\"ms-quantifier-kw\">");
        stringBuilder.append(displayLabel);
        stringBuilder.append("</span>");

        return stringBuilder.toString();
    }
}
