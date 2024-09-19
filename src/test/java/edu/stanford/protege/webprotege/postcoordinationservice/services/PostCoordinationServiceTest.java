package edu.stanford.protege.webprotege.postcoordinationservice.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.WebprotegePostcoordinationServiceServiceApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationViewEvent;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableConfiguration;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.MinioPostCoordinationDocumentLoader;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@SpringBootTest
@Import({WebprotegePostcoordinationServiceServiceApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationServiceTest {


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

    @BeforeEach
    public void setUp() throws IOException {
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

        assertEquals(29, specification.getNotAllowedAxes().size());
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

        assertEquals(29, specification.getDefaultAxes().size());
    }

    @Test
    public void GIVEN_existingFile_WHEN_firstImport_THEN_allEventsAreGenerated() {
        postCoordinationService.createFirstImport("postCoordinationImportFile.json", ProjectId.generate(), new UserId("alexsilaghi"));
        List<EntityPostCoordinationHistory> histories = mongoTemplate.findAll(EntityPostCoordinationHistory.class);
        assertNotNull(histories);
        assertEquals(3, histories.size());
        EntityPostCoordinationHistory history = histories.stream()
                        .filter(h -> h.getWhoficEntityIri().equalsIgnoreCase("http://id.who.int/icd/entity/257068234"))
                                .findFirst().orElse(null);
        assertNotNull(history);
        assertEquals(1, history.getPostCoordinationRevisions().size());
        assertEquals("alexsilaghi", history.getPostCoordinationRevisions().iterator().next().userId());
        assertNotNull(history.getPostCoordinationRevisions().iterator().next().postCoordinationEventList());
        Set<PostCoordinationViewEvent> viewEventSet = history.getPostCoordinationRevisions().iterator().next().postCoordinationEventList();
        assertEquals(11, viewEventSet.size());
        assertEquals(31, viewEventSet.iterator().next().axisEvents().size());
        assertNotNull(viewEventSet.iterator().next().linearizationView());

    }
}