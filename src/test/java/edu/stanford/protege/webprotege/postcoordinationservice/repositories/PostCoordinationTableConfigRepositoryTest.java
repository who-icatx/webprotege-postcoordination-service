package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.WebprotegePostcoordinationServiceServiceApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableAxisLabel;
import edu.stanford.protege.webprotege.postcoordinationservice.model.TableConfiguration;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({WebprotegePostcoordinationServiceServiceApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationTableConfigRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private PostCoordinationTableConfigRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void GIVEN_entityType_WHEN_fetchTheConfig_THEN_theCorrectConfigurationIsGiven() throws IOException {
        File tableConfig = new File("src/test/resources/postcoordinationTableConfig.json");
        List<Document> documents = objectMapper.readValue(tableConfig, new TypeReference<>() {
        });
        documents.forEach(document -> mongoTemplate.save(document, TableConfiguration.DEFINITIONS_COLLECTION));


        TableConfiguration tableConfiguration = repository.getTableConfigurationByEntityType("ExternalCauses");
        assertNotNull(tableConfiguration);
        assertNotNull(tableConfiguration.getPostCoordinationAxes());
        assertEquals(44, tableConfiguration.getPostCoordinationAxes().size());
        assertNotNull(tableConfiguration.getCompositePostCoordinationAxes());
        assertEquals(1, tableConfiguration.getCompositePostCoordinationAxes().size());
        assertEquals("http://id.who.int/icd/schema/levelOfConsciousness", tableConfiguration.getCompositePostCoordinationAxes().get(0).getPostCoordinationAxis());
        assertEquals(5, tableConfiguration.getCompositePostCoordinationAxes().get(0).getSubAxis().size());

    }


    @Test
    public void WHEN_fetchAllLabels_THEN_allLabelsAreCorrectlyFetched() throws IOException {

        File labels = new File("src/test/resources/postcoordinationAxisLabels.json");
        List<Document> documents  = objectMapper.readValue(labels, new TypeReference<>() {
        });
        documents.forEach(document -> mongoTemplate.save(document, TableAxisLabel.AXIS_LABELS_COLLECTION));



        List<TableAxisLabel> axisLabels = repository.getTableAxisLabels();

        assertNotNull(axisLabels);
        assertTrue(axisLabels.size() > 0);

    }
}
