package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import com.google.common.collect.ImmutableMap;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.entity.EntityNode;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationTableConfigRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.services.LinearizationService;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff.Revision2DiffElementsTranslator;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.nodeRendering.EntityRendererManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProjectChangesManagerTest {


    @Mock
    private Revision2DiffElementsTranslator revision2DiffElementsTranslator;
    @Mock
    private EntityRendererManager entityRendererManager;
    @Mock
    private PostCoordinationTableConfigRepository tableConfigurationRepo;
    @Mock
    private LinearizationService linearizationService;

    @Mock
    private CommandExecutor<GetIcatxEntityTypeRequest, GetIcatxEntityTypeResponse> entityTypeExecutor;


    @InjectMocks
    private ProjectChangesManager projectChangesManager;

    @BeforeEach
    public void setUp() {
        when(entityTypeExecutor.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new GetIcatxEntityTypeResponse(Arrays.asList("ICD"))));
    }

    @Test
    void GIVEN_noCustomScaleEvents_WHEN_getProjectChanges_THEN_returnEmptyChanges() {
        String whoficEntityIri = "http://id.who.int/icd/entity/123456";
        ProjectId projectId = ProjectId.generate();
        UserId userId = UserId.getGuest();
        PostCoordinationCustomScalesRevision customScaleRevision = mock(PostCoordinationCustomScalesRevision.class);
        when(customScaleRevision.postCoordinationEvents()).thenReturn(Set.of());
        when(customScaleRevision.userId()).thenReturn(userId);

        OWLEntity entity = new OWLClassImpl(IRI.create(whoficEntityIri));
        EntityNode entityNode = EntityNode.get(entity, "EntityName", ImmutableMap.of(), false, Set.of(), 0, List.of(), Set.of());

        when(entityRendererManager.getRenderedEntities(Set.of(whoficEntityIri), projectId))
                .thenReturn(List.of(entityNode));
        ProjectChangeForEntity result = projectChangesManager.getProjectChangesForCustomScaleRevision(projectId, whoficEntityIri, customScaleRevision, "");

        assertNotNull(result);
        assertEquals(0, result.projectChange().getChangeCount());
    }


    @Test
    void GIVEN_singleCustomScaleEvent_WHEN_getProjectChanges_THEN_returnSingleChange() {
        String whoficEntityIri = "http://id.who.int/icd/entity/123456";
        ProjectId projectId = ProjectId.generate();
        UserId userId = UserId.getGuest();
        PostCoordinationCustomScalesRevision customScaleRevision = mock(PostCoordinationCustomScalesRevision.class);

        PostCoordinationCustomScalesValueEvent event1 = mock(PostCoordinationCustomScalesValueEvent.class);
        when(customScaleRevision.postCoordinationEvents()).thenReturn(Set.of(event1));
        when(customScaleRevision.userId()).thenReturn(userId);
        when(event1.getPostCoordinationScaleValue()).thenReturn("http://id.who.int/icd/entity/1234567");
        when(event1.getPostCoordinationAxis()).thenReturn("http://id.who.int/icd/entity/someAxis");


        OWLEntity entity = new OWLClassImpl(IRI.create(whoficEntityIri));
        EntityNode entityNode = EntityNode.get(entity, "EntityName", ImmutableMap.of(), false, Set.of(), 0, List.of(), Set.of());

        when(entityRendererManager.getRenderedEntities(Set.of(whoficEntityIri), projectId))
                .thenReturn(List.of(entityNode));

        ProjectChangeForEntity result = projectChangesManager.getProjectChangesForCustomScaleRevision(projectId, whoficEntityIri, customScaleRevision, "");

        assertNotNull(result);
        assertEquals(1, result.projectChange().getChangeCount());
    }


    @Test
    void GIVEN_multipleCustomScaleEvents_WHEN_getProjectChanges_THEN_returnChangesForAllEvents() {
        String whoficEntityIri = "http://id.who.int/icd/entity/123456";
        ProjectId projectId = ProjectId.generate();
        UserId userId = UserId.getGuest();
        PostCoordinationCustomScalesRevision customScaleRevision = mock(PostCoordinationCustomScalesRevision.class);

        PostCoordinationCustomScalesValueEvent event1 = mock(PostCoordinationCustomScalesValueEvent.class);
        PostCoordinationCustomScalesValueEvent event2 = mock(PostCoordinationCustomScalesValueEvent.class);
        when(customScaleRevision.postCoordinationEvents()).thenReturn(Set.of(event1, event2));
        when(customScaleRevision.userId()).thenReturn(userId);
        when(event1.getPostCoordinationScaleValue()).thenReturn("http://id.who.int/icd/entity/1234567");
        when(event1.getPostCoordinationAxis()).thenReturn("http://id.who.int/icd/entity/someAxis");
        when(event2.getPostCoordinationScaleValue()).thenReturn("http://id.who.int/icd/entity/12345678");
        when(event2.getPostCoordinationAxis()).thenReturn("http://id.who.int/icd/entity/someAxis2");


        OWLEntity entity = new OWLClassImpl(IRI.create(whoficEntityIri));
        EntityNode entityNode = EntityNode.get(entity, "EntityName", ImmutableMap.of(), false, Set.of(), 0, List.of(), Set.of());

        when(entityRendererManager.getRenderedEntities(Set.of(whoficEntityIri), projectId))
                .thenReturn(List.of(entityNode));

        ProjectChangeForEntity result = projectChangesManager.getProjectChangesForCustomScaleRevision(projectId, whoficEntityIri, customScaleRevision, "");

        assertNotNull(result);
        assertEquals(2, result.projectChange().getChangeCount());
    }


    @Test
    void GIVEN_customScaleRevisions_WHEN_getProjectChangesForEntities_THEN_returnAllChanges() {
        String whoficEntityIri = "http://id.who.int/icd/entity/123456";
        ProjectId projectId = ProjectId.generate();
        UserId userId = UserId.valueOf("user123");

        PostCoordinationCustomScalesValueEvent scaleEvent1 = mock(PostCoordinationCustomScalesValueEvent.class);
        when(scaleEvent1.getPostCoordinationAxis()).thenReturn("Axis1");
        when(scaleEvent1.getUiDisplayName()).thenReturn("Scale Value 1");

        PostCoordinationCustomScalesValueEvent scaleEvent2 = mock(PostCoordinationCustomScalesValueEvent.class);
        when(scaleEvent2.getPostCoordinationAxis()).thenReturn("Axis2");
        when(scaleEvent2.getUiDisplayName()).thenReturn("Scale Value 2");

        PostCoordinationCustomScalesRevision revision1 = PostCoordinationCustomScalesRevision.create(userId, Set.of(scaleEvent1, scaleEvent2));

        EntityCustomScalesValuesHistory history = mock(EntityCustomScalesValuesHistory.class);
        when(history.getPostCoordinationCustomScalesRevisions()).thenReturn(List.of(revision1));
        when(history.getWhoficEntityIri()).thenReturn(whoficEntityIri);

        List<EntityCustomScalesValuesHistory> histories = List.of(history);

        OWLEntity entity = new OWLClassImpl(IRI.create(whoficEntityIri));
        EntityNode entityNode = EntityNode.get(entity, "EntityName", ImmutableMap.of(), false, Set.of(), 0, List.of(), Set.of());

        when(entityRendererManager.getRenderedEntities(Set.of(whoficEntityIri), projectId))
                .thenReturn(List.of(entityNode));

        Set<ProjectChangeForEntity> result = projectChangesManager.getProjectChangesForCustomScaleHistories(projectId, histories);

        assertNotNull(result);
        assertEquals(1, result.size());

        ProjectChangeForEntity projectChangeForEntity = result.iterator().next();
        assertEquals(whoficEntityIri, projectChangeForEntity.whoficEntityIri());
        assertEquals(2, projectChangeForEntity.projectChange().getChangeCount());
    }

    @Test
    void GIVEN_specRevisionHistory_WHEN_getProjectChanges_THEN_returnCorrectChanges() {
        String whoficEntityIri = "http://id.who.int/icd/entity/123456";
        ProjectId projectId = ProjectId.generate();
        UserId userId = UserId.valueOf("user123");

        PostCoordinationSpecificationEvent event1 = new AddToRequiredAxisEvent("Axis1", "LinearizationView");
        PostCoordinationSpecificationEvent event2 = new AddToDefaultAxisEvent("Axis2", "LinearizationView");

        PostCoordinationViewEvent viewEvent1 = new PostCoordinationViewEvent("LinearizationView", List.of(event1, event2));

        PostCoordinationSpecificationEvent event3 = new AddToRequiredAxisEvent("Axis1", "LinearizationView2");
        PostCoordinationSpecificationEvent event4 = new AddToDefaultAxisEvent("Axis2", "LinearizationView2");

        PostCoordinationViewEvent viewEvent2 = new PostCoordinationViewEvent("LinearizationView", List.of(event3, event4));

        PostCoordinationSpecificationRevision specRevision = PostCoordinationSpecificationRevision.create(userId, Set.of(viewEvent1, viewEvent2));

        OWLEntity entity = new OWLClassImpl(IRI.create(whoficEntityIri));
        EntityNode entityNode = EntityNode.get(entity, "EntityName", ImmutableMap.of(), false, Set.of(), 0, List.of(), Set.of());

        when(entityRendererManager.getRenderedEntities(Set.of(whoficEntityIri), projectId))
                .thenReturn(List.of(entityNode));

        ProjectChangeForEntity result = projectChangesManager.getProjectChangesForSpecRevision(projectId, whoficEntityIri, specRevision, "");

        assertNotNull(result);

        assertEquals(2, result.projectChange().getChangeCount(), "The change count should be 2, but was: " + result.projectChange().getChangeCount());
        assertEquals(whoficEntityIri, result.whoficEntityIri());
    }
}