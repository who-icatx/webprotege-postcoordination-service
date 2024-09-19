package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.jackson.WebProtegeJacksonApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.WebprotegePostcoordinationServiceServiceApplication;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import({WebprotegePostcoordinationServiceServiceApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationDocumentRepositoryTest {

    @Autowired
    PostCoordinationDocumentRepository documentRepository;

    @MockBean
    private MinioPostCoordinationDocumentLoader documentLoader;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        File initialFile = new File("src/test/resources/postCoordinationImportFile.json");
        InputStream targetStream = new FileInputStream(initialFile);
        when(documentLoader.fetchPostCoordinationDocument(eq("dummy"))).thenReturn(targetStream);
        objectMapper = new WebProtegeJacksonApplication().objectMapper(new OWLDataFactoryImpl());
    }


    @Test
    public void GIVEN_existingFile_WHEN_fetchTheSpecifications_THEN_specificationsAreCorrectlyMapped(){
        List<WhoficEntityPostCoordinationSpecification> postCoordinationSpecifications = documentRepository.fetchFromDocument("dummy").toList();

        assertNotNull(postCoordinationSpecifications);
        assertEquals(3, postCoordinationSpecifications.size());

        WhoficEntityPostCoordinationSpecification specification = postCoordinationSpecifications.stream()
                .filter(specification1 -> specification1.getWhoficEntityIri().equalsIgnoreCase("http://id.who.int/icd/entity/257068234"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("missing specification"));

        assertEquals("ICD", specification.getEntityType());
        assertEquals(11, specification.getPostCoordinationSpecifications().size());
    }

}
