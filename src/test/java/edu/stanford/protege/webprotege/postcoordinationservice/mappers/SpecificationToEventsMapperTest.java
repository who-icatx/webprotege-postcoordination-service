package edu.stanford.protege.webprotege.postcoordinationservice.mappers;

import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecificationRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.*;

@ExtendWith(MockitoExtension.class)
public class SpecificationToEventsMapperTest {


    @Test
    public void GIVEN_PostCoordinationSpecificationRequest_WHEN_map_THEN_fieldsAreTurnedIntoEvents() {
        PostCoordinationSpecificationRequest request = new PostCoordinationSpecificationRequest("view",
                Collections.singletonList("allowedAxes"),
                Collections.singletonList("defaultAxes"),
                Collections.singletonList("notAllowedAxes"),
                Collections.singletonList("requiredAxes")
        );

        List<PostCoordinationEvent> eventList = SpecificationToEventsMapper.convertFromSpecification(request);
        assertNotNull(eventList);
        assertEquals(4, eventList.size());

        assertTrue(eventList.stream()
                .filter(postCoordinationEvent -> postCoordinationEvent instanceof AddToAllowedAxisEvent)
                .map(postCoordinationEvent -> (AddToAllowedAxisEvent) postCoordinationEvent)
                .anyMatch(addToAllowedAxisEvent -> addToAllowedAxisEvent.getPostCoordinationAxis().equalsIgnoreCase("allowedAxes") &&
                        addToAllowedAxisEvent.getLinearizationView().equalsIgnoreCase("view")));

        assertTrue(eventList.stream()
                .filter(postCoordinationEvent -> postCoordinationEvent instanceof AddToDefaultAxisEvent)
                .map(postCoordinationEvent -> (AddToDefaultAxisEvent) postCoordinationEvent)
                .anyMatch(addToAllowedAxisEvent -> addToAllowedAxisEvent.getPostCoordinationAxis().equalsIgnoreCase("defaultAxes") &&
                        addToAllowedAxisEvent.getLinearizationView().equalsIgnoreCase("view")));


        assertTrue(eventList.stream()
                .filter(postCoordinationEvent -> postCoordinationEvent instanceof AddToRequiredAxisEvent)
                .map(postCoordinationEvent -> (AddToRequiredAxisEvent) postCoordinationEvent)
                .anyMatch(addToAllowedAxisEvent -> addToAllowedAxisEvent.getPostCoordinationAxis().equalsIgnoreCase("requiredAxes") &&
                        addToAllowedAxisEvent.getLinearizationView().equalsIgnoreCase("view")));

        assertTrue(eventList.stream()
                .filter(postCoordinationEvent -> postCoordinationEvent instanceof AddToNotAllowedAxisEvent)
                .map(postCoordinationEvent -> (AddToNotAllowedAxisEvent) postCoordinationEvent)
                .anyMatch(addToAllowedAxisEvent -> addToAllowedAxisEvent.getPostCoordinationAxis().equalsIgnoreCase("notAllowedAxes") &&
                        addToAllowedAxisEvent.getLinearizationView().equalsIgnoreCase("view")));

    }
}
