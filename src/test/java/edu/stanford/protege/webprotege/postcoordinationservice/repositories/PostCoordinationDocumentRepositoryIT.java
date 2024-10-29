package edu.stanford.protege.webprotege.postcoordinationservice.repositories;


import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficEntityPostCoordinationSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationDocumentRepositoryIT {

    @Autowired
    PostCoordinationDocumentRepository documentRepository;

    @MockBean
    private MinioPostCoordinationDocumentLoader documentLoader;

    @BeforeEach
    public void setUp() throws FileNotFoundException {
        File initialFile = new File("src/test/resources/postCoordinationImportFile.json");
        InputStream targetStream = new FileInputStream(initialFile);
        when(documentLoader.fetchPostCoordinationDocument(eq("dummy"))).thenReturn(targetStream);
    }


    @Test
    public void GIVEN_existingFile_WHEN_fetchTheSpecifications_THEN_specificationsAreCorrectlyMapped() {
        List<WhoficEntityPostCoordinationSpecification> postcoordinationSpecifications = documentRepository.fetchPostCoordinationSpecifications("dummy").toList();

        assertNotNull(postcoordinationSpecifications);
        assertEquals(3, postcoordinationSpecifications.size());

        WhoficEntityPostCoordinationSpecification specification = postcoordinationSpecifications.stream()
                .filter(specification1 -> specification1.whoficEntityIri().equalsIgnoreCase("http://id.who.int/icd/entity/257068234"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("missing specification"));

        assertEquals("ICD", specification.entityType());
        assertEquals(11, specification.postcoordinationSpecifications().size());
    }

}
