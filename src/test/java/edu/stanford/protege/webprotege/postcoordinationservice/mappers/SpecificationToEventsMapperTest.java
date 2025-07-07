package edu.stanford.protege.webprotege.postcoordinationservice.mappers;

import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationViewEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static junit.framework.TestCase.*;

@ExtendWith(MockitoExtension.class)
public class SpecificationToEventsMapperTest {


    @Test
    public void GIVEN_PostCoordinationSpecificationRequest_WHEN_map_THEN_fieldsAreTurnedIntoEvents() {
        PostCoordinationSpecification request = new PostCoordinationSpecification("view",
                Collections.singletonList("allowedAxes"),
                Collections.singletonList("defaultAxes"),
                Collections.singletonList("notAllowedAxes"),
                Collections.singletonList("requiredAxes")
        );

        List<PostCoordinationSpecificationEvent> eventList = SpecificationToEventsMapper.convertFromSpecification(request, new HashSet<>(Arrays.asList("view",
                "allowedAxes",
                "defaultAxes",
                "notAllowedAxes",
                "requiredAxes")));
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

    @Test
    public void GIVEN_newSpecification_WHEN_mappingToEvents_THEN_createEventsOnlyForDiffs(){
         PostCoordinationSpecification oldRequest = new PostCoordinationSpecification("view",
                Arrays.asList("http://id.who.int/icd/schema/associatedWith",
                        "http://id.who.int/icd/schema/infectiousAgent"),
                Collections.singletonList("http://id.who.int/icd/schema/activityWhenInjured"),
                new ArrayList<>(),
                new ArrayList<>()
        );
        WhoficEntityPostCoordinationSpecification oldSpec = new WhoficEntityPostCoordinationSpecification("entityIri","ICD", Collections.singletonList(oldRequest));

        PostCoordinationSpecification newRequest = new PostCoordinationSpecification("view",
                Arrays.asList("http://id.who.int/icd/schema/associatedWith",
                        "http://id.who.int/icd/schema/infectiousAgent",
                        "http://id.who.int/icd/schema/activityWhenInjured"),
                Collections.singletonList("http://id.who.int/icd/schema/occupationalDescriptor"),
                new ArrayList<>(),
                new ArrayList<>()
        );

        WhoficEntityPostCoordinationSpecification newSpec = new WhoficEntityPostCoordinationSpecification("entityIri","ICD", Collections.singletonList(newRequest));

        Set<PostCoordinationViewEvent> eventSet = SpecificationToEventsMapper.createEventsFromDiff(oldSpec, newSpec);
        assertEquals(1, eventSet.size());
        PostCoordinationViewEvent event = eventSet.iterator().next();
        assertEquals(2, event.axisEvents().size());
        assertEquals("view", event.linearizationView());

        Optional<PostCoordinationSpecificationEvent> addToAllowed = event.axisEvents().stream().filter(e -> e instanceof AddToAllowedAxisEvent).findFirst();
        assertTrue(addToAllowed.isPresent());
        assertEquals("http://id.who.int/icd/schema/activityWhenInjured", addToAllowed.get().getPostCoordinationAxis());

        Optional<PostCoordinationSpecificationEvent> addToDefault = event.axisEvents().stream().filter(e -> e instanceof AddToDefaultAxisEvent).findFirst();
        assertTrue(addToDefault.isPresent());
        assertEquals("http://id.who.int/icd/schema/occupationalDescriptor", addToDefault.get().getPostCoordinationAxis());

    }
}
