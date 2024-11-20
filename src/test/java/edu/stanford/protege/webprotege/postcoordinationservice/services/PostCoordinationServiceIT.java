package edu.stanford.protege.webprotege.postcoordinationservice.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.MinioPostCoordinationDocumentLoader;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.*;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.POSTCOORDINATION_CUSTOM_SCALES_COLLECTION;
import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.POSTCOORDINATION_HISTORY_COLLECTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationServiceIT {


    @Autowired
    private PostCoordinationService postCoordinationService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MinioPostCoordinationDocumentLoader documentLoader;

    @MockBean
    private LinearizationService linearizationService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private UserId userId;

    private ProjectId projectId;


    @BeforeEach
    public void setUp() throws IOException {
        mongoTemplate.dropCollection(POSTCOORDINATION_CUSTOM_SCALES_COLLECTION);
        mongoTemplate.dropCollection(POSTCOORDINATION_HISTORY_COLLECTION);

        when(documentLoader.fetchPostCoordinationDocument(eq("postCoordinationImportFile.json")))
                .thenReturn(new FileInputStream("src/test/resources/postCoordinationImportFile.json"));
        FileInputStream defintions = new FileInputStream("src/test/resources/LinearizationDefinitions.json");
        when(linearizationService.getLinearizationDefinitions())
                .thenReturn(objectMapper.readValue(defintions, new TypeReference<>() {
                }));

        File tableConfig = new File("src/test/resources/postcoordinationTableConfig.json");
        List<Document> documents = objectMapper.readValue(tableConfig, new TypeReference<>() {
        });
        documents.forEach(document -> mongoTemplate.save(document, TableConfiguration.DEFINITIONS_COLLECTION));

        userId = UserId.valueOf("alexsilaghi");
        projectId = ProjectId.generate();
    }

    @Test
    public void GIVEN_mainLinearization_WHEN_enriching_THEN_missingAxisAreSetToNotAllowed() throws IOException {

        File labels = new File("src/test/resources/LinearizationDefinitions.json");
        List<LinearizationDefinition> linearizationDefinitions = objectMapper.readValue(labels, new TypeReference<>() {
        });
        File tableConfig = new File("src/test/resources/postcoordinationTableConfig.json");
        List<TableConfiguration> tableConfigs = objectMapper.readValue(tableConfig, new TypeReference<>() {
        });

        PostCoordinationSpecification specification = new PostCoordinationSpecification(
                "http://id.who.int/icd/release/11/mms",
                Arrays.asList("http://id.who.int/icd/schema/hasSeverity", "http://id.who.int/icd/schema/medication"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        postCoordinationService.enrichWithMissingAxis("ICD", specification, linearizationDefinitions, tableConfigs);

        assertEquals(27, specification.getNotAllowedAxes().size());
    }


    @Test
    public void GIVEN_telescopicLinearization_WHEN_enriching_THEN_missingAxisAreSetToDefault() throws IOException {
        File labels = new File("src/test/resources/LinearizationDefinitions.json");
        List<LinearizationDefinition> linearizationDefinitions = objectMapper.readValue(labels, new TypeReference<>() {
        });
        File tableConfig = new File("src/test/resources/postcoordinationTableConfig.json");
        List<TableConfiguration> tableConfigs = objectMapper.readValue(tableConfig, new TypeReference<>() {
        });

        PostCoordinationSpecification specification = new PostCoordinationSpecification(
                "http://id.who.int/icd/release/11/ocu",
                Arrays.asList("http://id.who.int/icd/schema/hasSeverity", "http://id.who.int/icd/schema/medication"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        postCoordinationService.enrichWithMissingAxis("ICD", specification, linearizationDefinitions, tableConfigs);

        assertEquals(27, specification.getDefaultAxes().size());
    }

    @Test
    public void GIVEN_existingFile_WHEN_firstImport_THEN_allEventsAreGenerated() {
        postCoordinationService.createFirstSpecificationImport("postCoordinationImportFile.json", projectId, userId);
        List<EntityPostCoordinationHistory> histories = mongoTemplate.findAll(EntityPostCoordinationHistory.class);
        assertNotNull(histories);
        assertEquals(3, histories.size());
        EntityPostCoordinationHistory history = histories.stream()
                .filter(h -> h.getWhoficEntityIri().equalsIgnoreCase("http://id.who.int/icd/entity/257068234"))
                .findFirst().orElse(null);
        assertNotNull(history);
        assertEquals(1, history.getPostCoordinationRevisions().size());
        assertEquals(userId, history.getPostCoordinationRevisions().iterator().next().userId());
        assertNotNull(history.getPostCoordinationRevisions().iterator().next().postCoordinationEvents());
        Set<PostCoordinationViewEvent> viewEventSet = history.getPostCoordinationRevisions().iterator().next().postCoordinationEvents();
        assertEquals(16, viewEventSet.size());
        assertEquals(29, viewEventSet.iterator().next().axisEvents().size());
        assertNotNull(viewEventSet.iterator().next().linearizationView());

    }

    @Test
    public void GIVEN_noExistingHistory_WHEN_addCustomScaleRevision_THEN_createNewHistory() {
        var newScales = new WhoficCustomScalesValues(
                "http://id.who.int/icd/entity/12345",
                List.of(new PostCoordinationScaleCustomization(List.of("scaleValue1"), "axis1"))
        );
        postCoordinationService.addCustomScaleRevision(newScales, projectId, userId);

        List<EntityCustomScalesValuesHistory> histories = mongoTemplate.findAll(EntityCustomScalesValuesHistory.class);
        assertEquals(1, histories.size());

        EntityCustomScalesValuesHistory savedHistory = histories.get(0);
        assertEquals("http://id.who.int/icd/entity/12345", savedHistory.getWhoficEntityIri());
        assertEquals(1, savedHistory.getPostCoordinationCustomScalesRevisions().size());

        PostCoordinationCustomScalesRevision revision = savedHistory.getPostCoordinationCustomScalesRevisions().get(0);
        assertEquals(userId, revision.userId());
        assertEquals(1, revision.postCoordinationEvents().size());
    }

    @Test
    public void GIVEN_existingHistory_WHEN_addCustomScaleRevision_THEN_addToExistingRevision() {
        var newScales = new WhoficCustomScalesValues(
                "http://id.who.int/icd/entity/12345",
                List.of(new PostCoordinationScaleCustomization(List.of("scaleValue1"), "axis1"))
        );
        EntityCustomScalesValuesHistory initialHistory = new EntityCustomScalesValuesHistory(
                "http://id.who.int/icd/entity/12345",
                projectId.value(),
                Collections.singletonList(PostCoordinationCustomScalesRevision.create(userId, Collections.emptySet()))
        );
        mongoTemplate.save(initialHistory);

        postCoordinationService.addCustomScaleRevision(newScales, projectId, userId);

        List<EntityCustomScalesValuesHistory> histories = mongoTemplate.findAll(EntityCustomScalesValuesHistory.class);
        assertEquals(1, histories.size(), "Verify that no duplicate history is created, and the revision is added to the existing history");

        EntityCustomScalesValuesHistory updatedHistory = histories.get(0);
        assertEquals("http://id.who.int/icd/entity/12345", updatedHistory.getWhoficEntityIri());
        assertEquals(2, updatedHistory.getPostCoordinationCustomScalesRevisions().size());


        PostCoordinationCustomScalesRevision latestRevision = updatedHistory.getPostCoordinationCustomScalesRevisions().get(1);
        assertEquals(userId, latestRevision.userId());
        assertEquals(1, latestRevision.postCoordinationEvents().size());
    }
}