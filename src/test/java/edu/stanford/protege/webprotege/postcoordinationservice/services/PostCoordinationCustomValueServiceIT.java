package edu.stanford.protege.webprotege.postcoordinationservice.services;


import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetIcatxEntityTypeRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.GetIcatxEntityTypeResponse;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.MinioPostCoordinationDocumentLoader;
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
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
public class PostCoordinationCustomValueServiceIT {

    @MockBean
    private MinioPostCoordinationDocumentLoader documentLoader;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PostCoordinationService postCoordinationService;

    @MockBean
    private CommandExecutor<GetIcatxEntityTypeRequest, GetIcatxEntityTypeResponse> entityTypeExecutor;

    @BeforeEach
    public void setUp() throws IOException {
        when(documentLoader.fetchPostCoordinationDocument(eq("postCoordinationScalesImportFile.json")))
                .thenReturn(new FileInputStream("src/test/resources/postCoordinationScalesImportFile.json"));
        when(entityTypeExecutor.execute(any(), any())).thenReturn(CompletableFuture.supplyAsync(() -> new GetIcatxEntityTypeResponse(Arrays.asList("ICD"))));
    }


    @Test
    public void GIVEN_firstImportFile_WHEN_importing_THEN_eventsAreCorrectlyMapped() {

        postCoordinationService.crateFirstCustomScalesValuesImport("postCoordinationScalesImportFile.json", ProjectId.generate(), new UserId("alexsilaghi"));

        List<EntityCustomScalesValuesHistory> histories = mongoTemplate.findAll(EntityCustomScalesValuesHistory.class);
        assertNotNull(histories);
        assertEquals(19, histories.size());
        Optional<EntityCustomScalesValuesHistory> historyOptional = histories.stream()
                .filter(history -> history.getWhoficEntityIri().equalsIgnoreCase("http://id.who.int/icd/entity/515117475"))
                .findFirst();
        assertTrue(historyOptional.isPresent());
        assertNotNull(historyOptional.get().getPostCoordinationCustomScalesRevisions());
        assertEquals(1, historyOptional.get().getPostCoordinationCustomScalesRevisions().size());
        PostCoordinationCustomScalesRevision revision = historyOptional.get().getPostCoordinationCustomScalesRevisions().get(0);
        assertEquals(UserId.valueOf("alexsilaghi"), revision.userId());
        assertEquals(7, revision.postCoordinationEvents().size());
    }
}
