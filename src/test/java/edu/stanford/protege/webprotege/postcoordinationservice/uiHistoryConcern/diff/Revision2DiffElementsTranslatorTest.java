package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff;

import edu.stanford.protege.webprotege.diff.DiffElement;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationViewEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.services.LinearizationService;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Revision2DiffElementsTranslatorTest {

    private Revision2DiffElementsTranslator translator;
    private LinearizationService linearizationService;

    @BeforeEach
    void setUp() {
        linearizationService = mock(LinearizationService.class);
        translator = new Revision2DiffElementsTranslator(linearizationService);
    }

    @Test
    void GIVEN_emptyEventsMap_WHEN_getDiffElementsFromCustomScaleRevision_THEN_returnEmptyList() {
        Map<String, List<PostCoordinationCustomScalesValueEvent>> eventsByAxis = new HashMap<>();
        Map<String, Integer> orderedAxisMap = new HashMap<>();
        Map<String, String> entityIrisAndNames = new HashMap<>();

        List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> result = translator.getDiffElementsFromCustomScaleRevision(eventsByAxis, orderedAxisMap, entityIrisAndNames);

        assertTrue(result.isEmpty(), "Expected an empty result when no events are provided");
    }

    @Test
    void GIVEN_nonEmptyEventsMap_WHEN_getDiffElementsFromCustomScaleRevision_THEN_returnCorrectDiffElements() {
        Map<String, List<PostCoordinationCustomScalesValueEvent>> eventsByAxis = new HashMap<>();
        Map<String, Integer> orderedAxisMap = new HashMap<>();
        Map<String, String> entityIrisAndNames = new HashMap<>();

        PostCoordinationCustomScalesValueEvent event1 = new AddCustomScaleValueEvent("Axis1", "Scale1");

        PostCoordinationCustomScalesValueEvent event2 = new RemoveCustomScaleValueEvent("Axis2", "Scale2");

        eventsByAxis.put("Axis1", List.of(event1));
        eventsByAxis.put("Axis2", List.of(event2));

        orderedAxisMap.put("Axis1", 1);
        orderedAxisMap.put("Axis2", 2);

        entityIrisAndNames.put("Axis1", "Axis 1 Name");
        entityIrisAndNames.put("Axis2", "Axis 2 Name");

        List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> result = translator.getDiffElementsFromCustomScaleRevision(eventsByAxis, orderedAxisMap, entityIrisAndNames);

        assertEquals(2, result.size(), "Expected 2 diff elements");
        assertEquals("Axis 1 Name", result.get(0).getSourceDocument().getPostCoordinationName());
        assertEquals("Axis 2 Name", result.get(1).getSourceDocument().getPostCoordinationName());
    }


    @Test
    void GIVEN_missingAxisInEntityIrisAndNames_WHEN_getDiffElementsFromCustomScaleRevision_THEN_useAxisAsName() {
        Map<String, List<PostCoordinationCustomScalesValueEvent>> eventsByAxis = new HashMap<>();
        Map<String, Integer> orderedAxisMap = new HashMap<>();
        Map<String, String> entityIrisAndNames = new HashMap<>();

        PostCoordinationCustomScalesValueEvent event = new AddCustomScaleValueEvent("Axis3","Scale value");

        eventsByAxis.put("Axis3", List.of(event));
        orderedAxisMap.put("Axis3", 3);

        List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> result = translator.getDiffElementsFromCustomScaleRevision(eventsByAxis, orderedAxisMap, entityIrisAndNames);

        assertEquals(1, result.size(), "Expected 1 diff element");
        assertEquals("Axis3", result.get(0).getSourceDocument().getPostCoordinationName(), "Expected the axis name to be used as the postCoordinationName");
    }

    @Test
    void GIVEN_emptyChangesByView_WHEN_getDiffElementsFromSpecRevision_THEN_returnEmptyList() {
        List<PostCoordinationViewEvent> changesByView = new ArrayList<>();
        Map<String, Integer> orderedAxisMap = new HashMap<>();

        List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> result = translator.getDiffElementsFromSpecRevision(changesByView, orderedAxisMap);

        assertTrue(result.isEmpty(), "Expected an empty result when no changes are provided");
    }

    @Test
    void GIVEN_nonEmptyChangesByView_WHEN_getDiffElementsFromSpecRevision_THEN_returnCorrectDiffElements() {
        Map<String, Integer> orderedAxisMap = new HashMap<>();
        orderedAxisMap.put("Axis1", 1);
        orderedAxisMap.put("Axis2", 2);

        LinearizationDefinition linDef = mock(LinearizationDefinition.class);
        when(linDef.getLinearizationUri()).thenReturn("LinearizationView");
        when(linDef.getDisplayLabel()).thenReturn("Linearization View Label");
        when(linDef.getLinearizationId()).thenReturn("LinearizationViewID");

        when(linearizationService.getLinearizationDefinitions()).thenReturn(List.of(linDef));

        PostCoordinationSpecificationEvent event1 = mock(AddToRequiredAxisEvent.class);
        when(event1.getPostCoordinationAxis()).thenReturn("Axis1");
        PostCoordinationSpecificationEvent event2 = mock(AddToDefaultAxisEvent.class);
        when(event2.getPostCoordinationAxis()).thenReturn("Axis2");

        PostCoordinationViewEvent viewEvent = new PostCoordinationViewEvent("LinearizationView", List.of(event1, event2));
        List<PostCoordinationViewEvent> changesByView = List.of(viewEvent);

        List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> result = translator.getDiffElementsFromSpecRevision(changesByView, orderedAxisMap);

        assertEquals(1, result.size(), "Expected 1 diff element");
        assertEquals("Linearization View Label", result.get(0).getSourceDocument().getLinearizationViewName());
        assertEquals(2, result.get(0).getLineElement().size(), "Expected 2 events in the diff element");
    }

    @Test
    void GIVEN_unknownLinearizationView_WHEN_getDiffElementsFromSpecRevision_THEN_useLinearizationViewAsName() {
        Map<String, Integer> orderedAxisMap = new HashMap<>();
        orderedAxisMap.put("Axis1", 1);

        when(linearizationService.getLinearizationDefinitions()).thenReturn(List.of());

        PostCoordinationSpecificationEvent event1 = new AddToRequiredAxisEvent("Axis1","linView");

        PostCoordinationViewEvent viewEvent = new PostCoordinationViewEvent("UnknownView", List.of(event1));
        List<PostCoordinationViewEvent> changesByView = List.of(viewEvent);

        List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> result = translator.getDiffElementsFromSpecRevision(changesByView, orderedAxisMap);

        assertEquals(1, result.size(), "Expected 1 diff element");
        assertEquals("UnknownView", result.get(0).getSourceDocument().getLinearizationViewName(), "Expected the linearization view to be used as the name when no definition is found");
    }
}
