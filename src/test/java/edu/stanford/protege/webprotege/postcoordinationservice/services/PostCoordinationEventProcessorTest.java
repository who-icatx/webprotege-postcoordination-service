package edu.stanford.protege.webprotege.postcoordinationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.jackson.WebProtegeJacksonApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.WebprotegePostcoordinationServiceServiceApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationRevision;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@Import({WebprotegePostcoordinationServiceServiceApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationEventProcessorTest {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostCoordinationEventProcessor postCoordinationEventProcessor;

    private EntityPostCoordinationHistory entityPostCoordinationHistory;

    @BeforeEach
    public void setUp() throws IOException {
        File initialFile = new File("src/test/resources/processedPostCoordinationHistory.json");
        InputStream targetStream = new FileInputStream(initialFile);
        objectMapper = new WebProtegeJacksonApplication().objectMapper(new OWLDataFactoryImpl());
        entityPostCoordinationHistory = objectMapper.readValue(targetStream, EntityPostCoordinationHistory.class);
        List<PostCoordinationRevision> sortedRevisions = entityPostCoordinationHistory.getPostCoordinationRevisions()
                .stream()
                .toList()
                .stream()
                .sorted(Comparator.comparingLong(PostCoordinationRevision::timestamp))
                .toList();

        entityPostCoordinationHistory = new EntityPostCoordinationHistory(entityPostCoordinationHistory.getWhoficEntityIri(), entityPostCoordinationHistory.getProjectId(), sortedRevisions);
    }

    @Test
    public void GIVEN_savedDocument_WHEN_processingEvents_THEN_correctResponseIsGiven(){
        WhoficEntityPostCoordinationSpecification specification = postCoordinationEventProcessor.processHistory(entityPostCoordinationHistory);

        Optional<PostCoordinationSpecification> envSpec = specification.postCoordinationSpecifications().stream()
                .filter(spec -> spec.getLinearizationView().equalsIgnoreCase("http://id.who.int/icd/release/11/env"))
                        .findFirst();
        Optional<PostCoordinationSpecification> mmsSpec = specification.postCoordinationSpecifications().stream()
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
}
