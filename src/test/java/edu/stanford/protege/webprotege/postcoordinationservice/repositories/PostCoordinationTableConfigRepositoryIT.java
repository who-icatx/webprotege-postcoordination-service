package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetIcatxEntityTypeRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetIcatxEntityTypeResponse;
import edu.stanford.protege.webprotege.postcoordinationservice.handlers.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationTableConfigRepositoryIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PostCoordinationTableConfigRepository repository;


    @Autowired
    GetTablePostCoordinationAxisHandler handler;

    @MockBean
    private CommandExecutor<GetIcatxEntityTypeRequest, GetIcatxEntityTypeResponse> entityTypeExecutor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws IOException {
        File tableConfig = new File("src/test/resources/postcoordinationTableConfig.json");
        List<Document> documents = objectMapper.readValue(tableConfig, new TypeReference<>() {
        });
        documents.forEach(document -> mongoTemplate.save(document, TableConfiguration.DEFINITIONS_COLLECTION));
    }

    @AfterEach
    public void tearDown() {
        mongoTemplate.dropCollection(TableConfiguration.DEFINITIONS_COLLECTION);
    }

    @Test
    public void GIVEN_entityType_WHEN_fetchTheConfig_THEN_theCorrectConfigurationIsGiven() {


        when(entityTypeExecutor.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new GetIcatxEntityTypeResponse(Arrays.asList("ExternalCauses"))));
        GetTablePostCoordinationAxisResponse response = handler.handleRequest(new GetTablePostCoordinationAxisRequest(IRI.create("test"),
                ProjectId.generate()), new ExecutionContext()).block();

        assertNotNull(response);
        TableConfiguration tableConfiguration = response.tableConfiguration();

        assertNotNull(tableConfiguration);
        assertNotNull(tableConfiguration.getPostCoordinationAxes());
        assertEquals(42, tableConfiguration.getPostCoordinationAxes().size());
        assertNotNull(tableConfiguration.getCompositePostCoordinationAxes());
        assertEquals(1, tableConfiguration.getCompositePostCoordinationAxes().size());
        assertEquals("http://id.who.int/icd/schema/levelOfConsciousness", tableConfiguration.getCompositePostCoordinationAxes().get(0).getPostCoordinationAxis());
        assertEquals(4, tableConfiguration.getCompositePostCoordinationAxes().get(0).getSubAxis().size());

    }

    @Test
    void GIVEN_icdAndIchiEntityTypes_WHEN_fetchConfiguration_THEN_reunionOfTypesIsReturned(){

        when(entityTypeExecutor.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new GetIcatxEntityTypeResponse(Arrays.asList("ICD","ICHIIntervention"))));
        GetTablePostCoordinationAxisResponse response = handler.handleRequest(new GetTablePostCoordinationAxisRequest(IRI.create("test"),
                ProjectId.generate()), new ExecutionContext()).block();

        assertNotNull(response);
        TableConfiguration tableConfiguration = response.tableConfiguration();
        assertNotNull(tableConfiguration);
        assertNotNull(tableConfiguration.getPostCoordinationAxes());
        assertEquals(52, tableConfiguration.getPostCoordinationAxes().size());
        assertNotNull(tableConfiguration.getCompositePostCoordinationAxes());
        assertEquals(2, tableConfiguration.getCompositePostCoordinationAxes().size());
    }


    @Test
    public void WHEN_fetchAllLabels_THEN_allLabelsAreCorrectlyFetched() throws IOException {

        File labels = new File("src/test/resources/postcoordinationAxisLabels.json");
        List<Document> documents = objectMapper.readValue(labels, new TypeReference<>() {
        });
        documents.forEach(document -> mongoTemplate.save(document, TableAxisLabel.AXIS_LABELS_COLLECTION));


        List<TableAxisLabel> axisLabels = repository.getTableAxisLabels();

        assertNotNull(axisLabels);
        assertTrue(axisLabels.size() > 0);

    }
}
