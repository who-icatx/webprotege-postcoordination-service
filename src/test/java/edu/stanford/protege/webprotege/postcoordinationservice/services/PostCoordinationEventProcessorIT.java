package edu.stanford.protege.webprotege.postcoordinationservice.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.jackson.WebProtegeJacksonApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationRepository;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.POSTCOORDINATION_HISTORY_COLLECTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationEventProcessorIT {

    @MockBean
    private LinearizationService linearizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostCoordinationEventProcessor postCoordinationEventProcessor;

    @Autowired
    private PostCoordinationRepository repository;

    @Autowired
    private PostCoordinationService postCoordService;


    @MockBean
    private CommandExecutor<GetIcatxEntityTypeRequest, GetIcatxEntityTypeResponse> entityTypeExecutor;

    private EntityCustomScalesValuesHistory customScalesValuesHistory;

    @BeforeEach
    public void setUp() throws IOException {
        saveExistingHistory();
        saveExistingCustomScales();
        FileInputStream defintions = new FileInputStream("src/test/resources/LinearizationDefinitions.json");
        when(linearizationService.getLinearizationDefinitions())
                .thenReturn(objectMapper.readValue(defintions, new TypeReference<>() {
                }));

        when(entityTypeExecutor.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new GetIcatxEntityTypeResponse(Arrays.asList("ICD"))));
    }

    @Test
    public void GIVEN_savedDocument_WHEN_processingEvents_THEN_correctResponseIsGiven() {
        var history = repository.getExistingHistoryOrderedByRevision("http://id.who.int/icd/entity/2042704797", ProjectId.valueOf("b717d9a3-f265-46f5-bd15-9f1cf4b132c8"));

        assertTrue(history.isPresent());
        WhoficEntityPostCoordinationSpecification specification = postCoordinationEventProcessor.processHistory(history.get());

        Optional<PostCoordinationSpecification> envSpec = specification.postcoordinationSpecifications().stream()
                .filter(spec -> spec.getLinearizationView().equalsIgnoreCase("http://id.who.int/icd/release/11/env"))
                .findFirst();
        Optional<PostCoordinationSpecification> mmsSpec = specification.postcoordinationSpecifications().stream()
                .filter(spec -> spec.getLinearizationView().equalsIgnoreCase("http://id.who.int/icd/release/11/mms"))
                .findFirst();

        assertTrue(envSpec.isPresent());
        assertTrue(mmsSpec.isPresent());

        assertEquals(31, envSpec.get().getDefaultAxes().size());
        assertEquals(0, envSpec.get().getAllowedAxes().size());
        assertEquals(0, envSpec.get().getNotAllowedAxes().size());
        assertEquals(0, envSpec.get().getRequiredAxes().size());


        assertEquals(0, mmsSpec.get().getDefaultAxes().size());
        assertEquals(2, mmsSpec.get().getAllowedAxes().size());
        assertEquals(29, mmsSpec.get().getNotAllowedAxes().size());
        assertEquals(0, mmsSpec.get().getRequiredAxes().size());
    }


    @Test
    public void GIVEN_savedCustomScaleEvents_WHEN_processing_THEN_eventsAreCorrectlyProcessed() {
        var history = repository.getExistingCustomScaleHistoryOrderedByRevision(customScalesValuesHistory.getWhoficEntityIri(), ProjectId.valueOf("b717d9a3-f265-46f5-bd15-9f1cf4b132c8"));
        assertTrue(history.isPresent());

        WhoficCustomScalesValues response = postCoordinationEventProcessor.processCustomScaleHistory(history.get());
        assertNotNull(response);
        assertEquals(2, response.scaleCustomizations().size());

        Optional<PostCoordinationScaleCustomization> infectiousAgent = response.scaleCustomizations().stream()
                .filter(scale -> scale.getPostcoordinationAxis().equalsIgnoreCase("http://id.who.int/icd/schema/infectiousAgent"))
                .findFirst();
        assertTrue(infectiousAgent.isPresent());
        assertEquals(2, infectiousAgent.get().getPostcoordinationScaleValues().size());

        Optional<PostCoordinationScaleCustomization> associatedWith = response.scaleCustomizations().stream()
                .filter(scale -> scale.getPostcoordinationAxis().equalsIgnoreCase("http://id.who.int/icd/schema/associatedWith"))
                .findFirst();
        assertTrue(associatedWith.isPresent());
        assertEquals(1, associatedWith.get().getPostcoordinationScaleValues().size());
    }

    @Test
    public void test() {
        PostCoordinationScaleCustomization postCoordinationScaleCustomization = new
                PostCoordinationScaleCustomization(Arrays.asList("http://id.who.int/icd/entity/194483911", "http://id.who.int/icd/entity/5555555"), "http://id.who.int/icd/schema/infectiousAgent");
        WhoficCustomScalesValues customScalesValues = new WhoficCustomScalesValues(customScalesValuesHistory.getWhoficEntityIri(), Collections.singletonList(postCoordinationScaleCustomization));

        postCoordService.addCustomScaleRevision(customScalesValues, ProjectId.valueOf("b717d9a3-f265-46f5-bd15-9f1cf4b132c8"), UserId.valueOf("alexsilaghi"));
        var history = repository.getExistingCustomScaleHistoryOrderedByRevision(customScalesValuesHistory.getWhoficEntityIri(), ProjectId.valueOf("b717d9a3-f265-46f5-bd15-9f1cf4b132c8"));
        assertTrue(history.isPresent());

        WhoficCustomScalesValues response = postCoordinationEventProcessor.processCustomScaleHistory(history.get());
        System.out.println(response);
    }

    @Test
    public void WHEN_addingNewRevision_WHEN_fetchingTheProcessedData_THEN_revisionIsApplied() {

        PostCoordinationSpecification postCoordinationSpecification = new PostCoordinationSpecification("http://id.who.int/icd/release/11/mms",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                Collections.singletonList("http://id.who.int/icd/schema/infectiousAgent"));

        WhoficEntityPostCoordinationSpecification newSpec = new WhoficEntityPostCoordinationSpecification("http://id.who.int/icd/entity/2042704797",
                "ICD",
                Collections.singletonList(postCoordinationSpecification));

        postCoordService.addSpecificationRevision(newSpec, UserId.valueOf("alexsilaghi"), ProjectId.valueOf("b717d9a3-f265-46f5-bd15-9f1cf4b132c8"));

        var history = repository.getExistingHistoryOrderedByRevision("http://id.who.int/icd/entity/2042704797", ProjectId.valueOf("b717d9a3-f265-46f5-bd15-9f1cf4b132c8"));
        assertTrue(history.isPresent());

        WhoficEntityPostCoordinationSpecification specification = postCoordinationEventProcessor.processHistory(history.get());

        assertNotNull(specification);

        Optional<PostCoordinationSpecification> mms = specification.postcoordinationSpecifications().stream()
                .filter(spec -> spec.getLinearizationView().equalsIgnoreCase("http://id.who.int/icd/release/11/mms"))
                .findFirst();

        assertTrue(mms.isPresent());
        assertEquals(1, mms.get().getRequiredAxes().size());
        assertEquals(28, mms.get().getNotAllowedAxes().size());
        assertEquals(2, mms.get().getAllowedAxes().size());
        assertEquals("http://id.who.int/icd/schema/infectiousAgent", mms.get().getRequiredAxes().get(0));
    }


    private void saveExistingHistory() throws IOException {
        File initialFile = new File("src/test/resources/processedPostCoordinationHistory.json");
        objectMapper = new WebProtegeJacksonApplication().objectMapper(new OWLDataFactoryImpl());
        EntityPostCoordinationHistory entityPostCoordinationHistory = objectMapper.readValue(initialFile, EntityPostCoordinationHistory.class);
        List<PostCoordinationSpecificationRevision> sortedRevisions = entityPostCoordinationHistory.getPostCoordinationRevisions()
                .stream()
                .toList()
                .stream()
                .sorted(Comparator.comparingLong(PostCoordinationSpecificationRevision::timestamp))
                .toList();

        entityPostCoordinationHistory = new EntityPostCoordinationHistory(entityPostCoordinationHistory.getWhoficEntityIri(), entityPostCoordinationHistory.getProjectId(), sortedRevisions);

        repository.writeDocument(objectMapper.convertValue(entityPostCoordinationHistory, Document.class), POSTCOORDINATION_HISTORY_COLLECTION);

    }

    private void saveExistingCustomScales() throws IOException {

        File processedCustomScales = new File("src/test/resources/processedCustomValuesEvents.json");
        Document existingData = objectMapper.readValue(processedCustomScales, Document.class);
        repository.writeDocument(existingData, EntityCustomScalesValuesHistory.POSTCOORDINATION_CUSTOM_SCALES_COLLECTION);
        this.customScalesValuesHistory = objectMapper.convertValue(existingData, EntityCustomScalesValuesHistory.class);
    }

}
