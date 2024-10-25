package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PostCoordinationEventProcessorTest {

    private PostCoordinationEventProcessor eventProcessor;

    @BeforeEach
    public void setUp() {
        eventProcessor = new PostCoordinationEventProcessor();
    }

    @Test
    void GIVEN_customScalesHistoryWithEvents_WHEN_processCustomScaleHistory_THEN_correctCustomizationsReturned() {
        List<PostCoordinationCustomScalesValueEvent> events1 = List.of(
                new AddCustomScaleValueEvent("axis1", "value1"),
                new AddCustomScaleValueEvent("axis1", "value2")
        );

        List<PostCoordinationCustomScalesValueEvent> events2 = List.of(
                new RemoveCustomScaleValueEvent("axis1", "value1")  // Now "axis1" exists before removal
        );

        EntityCustomScalesValuesHistory customScalesHistory = new EntityCustomScalesValuesHistory(
                "entity1",
                ProjectId.generate().toString(),
                List.of(
                        new PostCoordinationCustomScalesRevision(UserId.getGuest(), 1L, new HashSet<>(events1)),
                        new PostCoordinationCustomScalesRevision(UserId.getGuest(), 2L, new HashSet<>(events2))
                )
        );

        WhoficCustomScalesValues result = eventProcessor.processCustomScaleHistory(customScalesHistory);

        assertNotNull(result);
        assertEquals(1, result.scaleCustomizations().size());

        Optional<PostCoordinationScaleCustomization> axis1Customization = result.scaleCustomizations().stream()
                .filter(scale -> scale.getPostcoordinationAxis().equals("axis1"))
                .findFirst();

        assertTrue(axis1Customization.isPresent());
        assertEquals(1, axis1Customization.get().getPostcoordinationScaleValues().size());
        assertEquals("value2", axis1Customization.get().getPostcoordinationScaleValues().get(0));
    }


    @Test
    void GIVEN_specificationHistoryWithEvents_WHEN_processHistory_THEN_correctSpecificationReturned() {
        List<PostCoordinationSpecificationEvent> events1 = List.of(
                new AddToRequiredAxisEvent("axis1", "view1"),
                new AddToDefaultAxisEvent("axis2", "view1")
        );
        List<PostCoordinationSpecificationEvent> events2 = List.of(
                new AddToNotAllowedAxisEvent("axis1", "view1")
        );

        EntityPostCoordinationHistory history = new EntityPostCoordinationHistory(
                "entity1",
                "project1",
                List.of(
                        new PostCoordinationSpecificationRevision(UserId.getGuest(), 1L, Set.of(new PostCoordinationViewEvent("view1", events1))),
                        new PostCoordinationSpecificationRevision(UserId.getGuest(), 2L, Set.of(new PostCoordinationViewEvent("view1", events2)))
                )
        );

        WhoficEntityPostCoordinationSpecification result = eventProcessor.processHistory(history);

        assertNotNull(result);
        assertEquals("entity1", result.whoficEntityIri());
        assertEquals(1, result.postcoordinationSpecifications().size());

        PostCoordinationSpecification specification = result.postcoordinationSpecifications().get(0);
        assertEquals("view1", specification.getLinearizationView());
        assertTrue(specification.getNotAllowedAxes().contains("axis1"));
        assertFalse(specification.getRequiredAxes().contains("axis1"));
        assertTrue(specification.getDefaultAxes().contains("axis2"));
    }

    @Test
    void GIVEN_emptyCustomScalesHistory_WHEN_processCustomScaleHistory_THEN_noCustomizationsReturned() {
        EntityCustomScalesValuesHistory emptyCustomScalesHistory = new EntityCustomScalesValuesHistory("entity1", ProjectId.generate().toString(), Collections.emptyList());

        WhoficCustomScalesValues result = eventProcessor.processCustomScaleHistory(emptyCustomScalesHistory);

        assertNotNull(result);
        assertEquals(0, result.scaleCustomizations().size());
    }

    @Test
    void GIVEN_emptyPostCoordinationHistory_WHEN_processHistory_THEN_noSpecificationsReturned() {
        EntityPostCoordinationHistory emptyHistory = new EntityPostCoordinationHistory("entity1", "project1", Collections.emptyList());

        WhoficEntityPostCoordinationSpecification result = eventProcessor.processHistory(emptyHistory);

        assertNotNull(result);
        assertEquals("entity1", result.whoficEntityIri());
        assertEquals(0, result.postcoordinationSpecifications().size());
    }
}
