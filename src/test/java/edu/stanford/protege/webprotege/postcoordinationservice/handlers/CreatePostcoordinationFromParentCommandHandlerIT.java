package edu.stanford.protege.webprotege.postcoordinationservice.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.postcoordinationservice.IntegrationTest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationDocumentRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationTableConfigRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.services.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.semanticweb.owlapi.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ExtendWith({SpringExtension.class, IntegrationTest.class})
@ActiveProfiles("test")
class CreatePostcoordinationFromParentCommandHandlerIT {

    @Autowired
    private CreatePostcoordinationFromParentCommandHandler handler;

    @Autowired
    private PostCoordinationEventProcessor eventProcessor;

    @Autowired
    private PostCoordinationRepository repository;

    private PostCoordinationService postCoordService;

    @Autowired
    private PostCoordinationDocumentRepository postCoordinationDocumentRepository;
    @Autowired
    private ReadWriteLockService readWriteLockService;

    @MockBean
    private LinearizationService linearizationService;

    @Autowired
    private NewRevisionsEventEmitterService newRevisionsEventEmitterService;

    @MockBean
    private PostCoordinationTableConfigRepository postCoordinationTableConfigRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private ProjectId projectId;
    private ExecutionContext executionContext;
    private String parentEntityIri;
    private String newEntityIri;

    @BeforeEach
    void setUp() throws IOException {
        projectId = ProjectId.generate();
        executionContext = new ExecutionContext(UserId.valueOf("testUser"), "testToken");
        parentEntityIri = "http://example.org/parentEntity";
        newEntityIri = "http://example.org/newEntity";
        FileInputStream defintions = new FileInputStream("src/test/resources/LinearizationDefinitions.json");
        when(linearizationService.getLinearizationDefinitions())
                .thenReturn(objectMapper.readValue(defintions, new TypeReference<>() {
                }));

        File tableConfig = new File("src/test/resources/postcoordinationTableConfig.json");
        List<TableConfiguration> documents = objectMapper.readValue(tableConfig, new TypeReference<>() {
        });
        when(postCoordinationTableConfigRepository.getALlTableConfiguration()).thenReturn(documents);
        mongoTemplate.getDb().drop();
        postCoordService = new PostCoordinationService(repository,
                postCoordinationTableConfigRepository,
                linearizationService,
                readWriteLockService,
                postCoordinationDocumentRepository,
                objectMapper,
                newRevisionsEventEmitterService,
                eventProcessor);
    }

    @Test
    void GIVEN_validParentWithCoreLinId_WHEN_handleRequest_THEN_defaultAxesShouldBeInherited() {
        CreatePostcoordinationFromParentRequest request = CreatePostcoordinationFromParentRequest.create(
                IRI.create(newEntityIri), IRI.create(parentEntityIri), projectId
        );

        WhoficEntityPostCoordinationSpecification parentSpec = new WhoficEntityPostCoordinationSpecification(
                parentEntityIri, "ICD", List.of(
                new PostCoordinationSpecification("http://id.who.int/icd/release/11/mms", List.of("axis1"), List.of("axis2"), List.of(), List.of())
        ));
        postCoordService.addSpecificationRevision(parentSpec, UserId.getGuest(), projectId);

        handler.handleRequest(request, executionContext).block();

        var specHistoryOptional = repository.getExistingHistoryOrderedByRevision(newEntityIri, projectId);
        assertTrue(specHistoryOptional.isPresent(), "No history was created");
        var savedSpec = eventProcessor.processHistory(specHistoryOptional.get());

        assertNotNull(savedSpec, "The new specification should be saved.");
        assertEquals(newEntityIri, savedSpec.whoficEntityIri(), "The saved specification should match the new entity IRI.");

        PostCoordinationSpecification inheritedSpec = savedSpec.postcoordinationSpecifications().get(0);
        assertEquals(List.of("axis1", "axis2"), inheritedSpec.getNotAllowedAxes(), "Axes inherited from parent should have not allowed values for main linearizations");
        assertTrue(inheritedSpec.getDefaultAxes().isEmpty(), "Axes inherited from parent should have default values for telescopic linearizations");
    }


    @Test
    void GIVEN_validParentWithoutCoreLinId_WHEN_handleRequest_THEN_notAllowedAxesShouldBeInherited() {
        CreatePostcoordinationFromParentRequest request = CreatePostcoordinationFromParentRequest.create(
                IRI.create(newEntityIri), IRI.create(parentEntityIri), projectId
        );

        WhoficEntityPostCoordinationSpecification parentSpec = WhoficEntityPostCoordinationSpecification.create(
                parentEntityIri, "ICD", List.of(
                        new PostCoordinationSpecification("http://id.who.int/icd/release/11/icd-o", List.of("axis1"), List.of(), List.of(), List.of())
                ));
        postCoordService.addSpecificationRevision(parentSpec, UserId.getGuest(), projectId);


        handler.handleRequest(request, executionContext).block();

        var specHistoryOptional = repository.getExistingHistoryOrderedByRevision(newEntityIri, projectId);
        assertTrue(specHistoryOptional.isPresent(), "No history was created");
        var savedSpec = eventProcessor.processHistory(specHistoryOptional.get());

        assertNotNull(savedSpec, "The new specification should be saved.");
        assertEquals(newEntityIri, savedSpec.whoficEntityIri(), "The saved specification should match the new entity IRI.");

        PostCoordinationSpecification inheritedSpec = savedSpec.postcoordinationSpecifications().get(0);
        assertTrue(inheritedSpec.getDefaultAxes().isEmpty(), "defaultAxes should be empty for main linearizations");
        assertEquals(List.of("axis1"), inheritedSpec.getNotAllowedAxes(), "notAllowedAxes should inherit from the parent all axes when linearization view is main linearization");
    }

    @Test
    void GIVEN_invalidParentEntity_WHEN_handleRequest_THEN_noSpecificationShouldBeSaved() {
        CreatePostcoordinationFromParentRequest request = CreatePostcoordinationFromParentRequest.create(
                IRI.create(newEntityIri), IRI.create("http://invalid-parent-iri"), projectId
        );

        handler.handleRequest(request, executionContext).block();

        Query query = new Query();
        query.addCriteria(Criteria.where("whoficEntityIri").is(newEntityIri));
        WhoficEntityPostCoordinationSpecification savedSpec = mongoTemplate.findOne(query, WhoficEntityPostCoordinationSpecification.class);

        assertNull(savedSpec, "No specification should have been saved when the parent entity is invalid.");
    }

    @Test
    void GIVEN_parentWithMultipleLinearizations_WHEN_handleRequest_THEN_axesShouldBeInheritedForEachView() {
        CreatePostcoordinationFromParentRequest request = CreatePostcoordinationFromParentRequest.create(
                IRI.create(newEntityIri), IRI.create(parentEntityIri), projectId
        );

        WhoficEntityPostCoordinationSpecification parentSpec = new WhoficEntityPostCoordinationSpecification(
                parentEntityIri, "ICD", List.of(
                new PostCoordinationSpecification("http://id.who.int/icd/release/11/mms", List.of("axis1"), List.of(), List.of(), List.of()),
                new PostCoordinationSpecification("http://id.who.int/icd/release/11/pch", List.of("axis2"), List.of(), List.of(), List.of())
        ));
        postCoordService.addSpecificationRevision(parentSpec, UserId.getGuest(), projectId);


        handler.handleRequest(request, executionContext).block();

        var specHistoryOptional = repository.getExistingHistoryOrderedByRevision(newEntityIri, projectId);
        assertTrue(specHistoryOptional.isPresent(), "No history was created");
        var savedSpec = eventProcessor.processHistory(specHistoryOptional.get());

        assertNotNull(savedSpec, "The new specification should be saved.");
        assertEquals(2, savedSpec.postcoordinationSpecifications().size(), "There should be two linearization specifications.");

        for (PostCoordinationSpecification inheritedSpec : savedSpec.postcoordinationSpecifications()) {
            if (inheritedSpec.getLinearizationView().equals("http://id.who.int/icd/release/11/mms")) {
                assertEquals(List.of("axis1"), inheritedSpec.getNotAllowedAxes(), "Should inherit axes for MMS (main linearization) and have not allowed value.");
            } else if (inheritedSpec.getLinearizationView().equals("http://id.who.int/icd/release/11/pch")) {
                assertEquals(List.of("axis2"), inheritedSpec.getDefaultAxes(), "Should inherit axes for PCH (telescopic linearization) and have default value");
            }
        }
    }

    @Test
    void GIVEN_validRequest_WHEN_handleRequestTwice_THEN_noDuplicateSpecificationsShouldBeSaved() {
        CreatePostcoordinationFromParentRequest request = CreatePostcoordinationFromParentRequest.create(
                IRI.create(newEntityIri), IRI.create(parentEntityIri), projectId
        );

        WhoficEntityPostCoordinationSpecification parentSpec = new WhoficEntityPostCoordinationSpecification(
                parentEntityIri, "ICD", List.of(
                new PostCoordinationSpecification("http://id.who.int/icd/release/11/mms", List.of("axis1"), List.of(), List.of(), List.of())
        ));
        postCoordService.addSpecificationRevision(parentSpec, UserId.getGuest(), projectId);


        handler.handleRequest(request, executionContext).block();
        handler.handleRequest(request, executionContext).block();

        Query query = new Query();
        query.addCriteria(Criteria.where("whoficEntityIri").is(newEntityIri));
        EntityPostCoordinationHistory savedHistory = mongoTemplate.findOne(query, EntityPostCoordinationHistory.class);

        assertNotNull(savedHistory);
        assertEquals(1, savedHistory.getPostCoordinationRevisions().size(), "There should only be one saved revision.");
    }


}


