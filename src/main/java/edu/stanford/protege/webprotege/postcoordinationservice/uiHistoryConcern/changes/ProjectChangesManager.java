package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import edu.stanford.protege.webprotege.change.ProjectChange;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.diff.DiffElement;
import edu.stanford.protege.webprotege.entity.EntityNode;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationTableConfigRepository;
import edu.stanford.protege.webprotege.postcoordinationservice.services.LinearizationService;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff.*;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.nodeRendering.EntityRendererManager;
import edu.stanford.protege.webprotege.revision.RevisionNumber;
import org.slf4j.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.*;

import static edu.stanford.protege.webprotege.postcoordinationservice.mappers.SpecificationToEventsMapper.groupScaleEventsByAxis;


@Component
public class ProjectChangesManager {

    private static final Logger logger = LoggerFactory.getLogger(ProjectChangesManager.class);

    private final Revision2DiffElementsTranslator revision2DiffElementsTranslator;

    private final EntityRendererManager entityRendererManager;

    private final PostCoordinationTableConfigRepository tableConfigurationRepo;

    private final LinearizationService linearizationService;


    public ProjectChangesManager(
            Revision2DiffElementsTranslator revision2DiffElementsTranslator,
            EntityRendererManager entityRendererManager,
            PostCoordinationTableConfigRepository tableConfigurationRepo,
            LinearizationService linearizationService) {
        this.revision2DiffElementsTranslator = revision2DiffElementsTranslator;
        this.entityRendererManager = entityRendererManager;
        this.tableConfigurationRepo = tableConfigurationRepo;
        this.linearizationService = linearizationService;
    }

    private ProjectChange getProjectChangesForCustomScaleRevision(PostCoordinationCustomScalesRevision revision,
                                                                  String subjectName,
                                                                  List<EntityNode> renderedEntities) {
        final int totalChanges;
        var changesByAxis = groupScaleEventsByAxis(revision.postCoordinationEventList().stream().toList());
        totalChanges = changesByAxis.size();
        List<TableAxisLabel> tableAxisLabels = tableConfigurationRepo.getTableAxisLabels();

        List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> diffElements = revision2DiffElementsTranslator.getDiffElementsFromCustomScaleRevision(changesByAxis, createOrderAxisListWithSubAxis(), tableAxisLabels);
        List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> mutableDiffElements = new ArrayList<>(diffElements);
        mutableDiffElements.sort(Comparator.comparing(diffElement -> diffElement.getSourceDocument().getSortingCode()));

        List<DiffElement<String, String>> renderedDiffElements = renderDiffElementsForCustomScale(mutableDiffElements, renderedEntities);
        int pageElements = renderedDiffElements.size();
        int pageCount;
        if (pageElements == 0) {
            pageCount = 1;
        } else {
            pageCount = totalChanges / pageElements + (totalChanges % pageElements);
        }
        Page<DiffElement<String, String>> page = Page.create(
                1,
                pageCount,
                renderedDiffElements,
                totalChanges
        );
        ProjectChange projectChange = ProjectChange.get(
                RevisionNumber.valueOf("0"),
                revision.userId(),
                revision.timestamp(),
                "Edited Postcoordination Scale Values for Entity: " + subjectName,
                totalChanges,
                page);
        return projectChange;
    }

    public ProjectChangeForEntity getProjectChangesForCustomScaleRevision(ProjectId projectId, String whoficEntityIri, PostCoordinationCustomScalesRevision revision) {
        Map<String, String> entityIrisAndNames = new HashMap<>();
        entityIrisAndNames.put(whoficEntityIri, whoficEntityIri);
        revision.postCoordinationEventList()
                .forEach(event -> entityIrisAndNames.put(event.getPostCoordinationScaleValue(), event.getPostCoordinationScaleValue()));
        List<EntityNode> renderedEntitiesList = entityRendererManager.getRenderedEntities(entityIrisAndNames.keySet(), projectId);

        var entityTextOptional = renderedEntitiesList
                .stream()
                .filter(entityNode -> entityNode.getEntity().getIRI().toString().equals(whoficEntityIri))
                .map(EntityNode::getBrowserText)
                .findFirst();
        entityTextOptional.ifPresent(browserText -> entityIrisAndNames.put(whoficEntityIri, browserText));

        ProjectChange projectChange = getProjectChangesForCustomScaleRevision(
                revision,
                entityIrisAndNames.get(whoficEntityIri),
                renderedEntitiesList
        );

        ProjectChangeForEntity projectChangeForEntity = ProjectChangeForEntity.create(
                whoficEntityIri,
                projectChange
        );

        return projectChangeForEntity;
    }

    private List<DiffElement<String, String>> renderDiffElementsForCustomScale(List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> diffElements, List<EntityNode> renderedEntities) {

        List<DiffElement<String, String>> renderedDiffElements = new ArrayList<>();
        DiffElementRenderer<String> renderer = new DiffElementRenderer<>(renderedEntities);
        for (DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent> diffElement : diffElements) {
            renderedDiffElements.add(renderer.render(diffElement));
        }
        return renderedDiffElements;
    }

    public Set<ProjectChangeForEntity> getProjectChangesForCustomScaleHistories(ProjectId projectId, List<EntityCustomScalesValuesHistory> entityCustomScaleHistories) {
        Map<String, String> entityIrisAndNames = new HashMap<>();
        Set<CustomScaleRevisionWithEntity> scaleRevisions = entityCustomScaleHistories.stream()
                .flatMap(history ->
                        history.getPostCoordinationCustomScalesRevisions()
                                .stream()
                                .map(revision -> new CustomScaleRevisionWithEntity(revision, history.getWhoficEntityIri()))
                )
                .sorted(Comparator.comparing(CustomScaleRevisionWithEntity::getRevision))
                .peek(revisionWithEntity -> {
                    entityIrisAndNames.put(revisionWithEntity.getWhoficEntityIri(), revisionWithEntity.getWhoficEntityIri());
                    revisionWithEntity.getRevision().postCoordinationEventList()
                            .forEach(event -> entityIrisAndNames.put(event.getPostCoordinationScaleValue(), event.getPostCoordinationScaleValue()));
                })
                .collect(Collectors.toSet());

        List<EntityNode> renderedEntitiesList = entityRendererManager.getRenderedEntities(entityIrisAndNames.keySet(), projectId);

        renderedEntitiesList.forEach(renderedEntity -> {
            if (entityIrisAndNames.get(renderedEntity.getEntity().toStringID()) != null) {
                entityIrisAndNames.put(renderedEntity.getEntity().toStringID(), renderedEntity.getBrowserText());
            }
        });

        Set<ProjectChangeForEntity> projectChangeForEntityList = scaleRevisions.stream()
                .flatMap(revisionWithEntity -> {
                            ProjectChange projectChange = getProjectChangesForCustomScaleRevision(
                                    revisionWithEntity.getRevision(),
                                    entityIrisAndNames.get(revisionWithEntity.getWhoficEntityIri()),
                                    renderedEntitiesList
                            );
                            ProjectChangeForEntity projectChangeForEntity = ProjectChangeForEntity.create(
                                    revisionWithEntity.getWhoficEntityIri(),
                                    projectChange
                            );
                            return Stream.of(projectChangeForEntity);
                        }
                )
                .collect(Collectors.toSet());

        return projectChangeForEntityList;
    }


    private List<String> createOrderAxisListWithSubAxis() {
        TableConfiguration tableConfiguration = tableConfigurationRepo.getTableConfigurationByEntityType("ICD");
        List<String> orderedAxisList = new LinkedList<>(tableConfiguration.getPostCoordinationAxes());
        List<CompositeAxis> compositeAxisList = new ArrayList<>(tableConfiguration.getCompositePostCoordinationAxes());

        compositeAxisList.forEach(compositeAxis ->
                {
                    int indexForCurrAxis = orderedAxisList.indexOf(compositeAxis.getPostCoordinationAxis());
                    List<String> subAxisList = new LinkedList<>(compositeAxis.getSubAxis());
                    orderedAxisList.addAll(indexForCurrAxis + 1, subAxisList);
                    orderedAxisList.remove(indexForCurrAxis);
                }
        );
        return orderedAxisList;
    }

    public Set<ProjectChangeForEntity> getProjectChangesForSpecHistories(ProjectId projectId, List<EntityPostCoordinationHistory> entitySpecHistories) {
        Map<String, String> entityIrisAndNames = new HashMap<>();
        Set<SpecRevisionWithEntity> specRevisions = entitySpecHistories.stream()
                .flatMap(history ->
                        history.getPostCoordinationRevisions()
                                .stream()
                                .map(revision -> new SpecRevisionWithEntity(revision, history.getWhoficEntityIri()))
                )
                .sorted(Comparator.comparing(SpecRevisionWithEntity::getRevision))
                .peek(revisionWithEntity -> {
                    entityIrisAndNames.put(revisionWithEntity.getWhoficEntityIri(), revisionWithEntity.getWhoficEntityIri());
                })
                .collect(Collectors.toSet());

        List<EntityNode> renderedEntitiesList = entityRendererManager.getRenderedEntities(entityIrisAndNames.keySet(), projectId);
        renderedEntitiesList.forEach(renderedEntity -> {
            if (entityIrisAndNames.get(renderedEntity.getEntity().toStringID()) != null) {
                entityIrisAndNames.put(renderedEntity.getEntity().toStringID(), renderedEntity.getBrowserText());
            }
        });

        Set<ProjectChangeForEntity> projectChangeForEntityList = specRevisions.stream()
                .flatMap(revisionWithEntity -> {
                            ProjectChange projectChange = getProjectChangesForSpecRevision(
                                    revisionWithEntity.getRevision(),
                                    entityIrisAndNames.get(revisionWithEntity.getWhoficEntityIri()),
                                    renderedEntitiesList
                            );
                            ProjectChangeForEntity projectChangeForEntity = ProjectChangeForEntity.create(
                                    revisionWithEntity.getWhoficEntityIri(),
                                    projectChange
                            );
                            return Stream.of(projectChangeForEntity);
                        }
                )
                .collect(Collectors.toSet());

        return projectChangeForEntityList;
    }

    private ProjectChange getProjectChangesForSpecRevision(PostCoordinationSpecificationRevision revision,
                                                           String subjectName,
                                                           List<EntityNode> renderedEntities) {
        final int totalChanges;
        var eventsByView = revision.postCoordinationEventList().stream().toList();
        totalChanges = eventsByView.size();
        List<TableAxisLabel> tableAxisLabels = tableConfigurationRepo.getTableAxisLabels();

        List<DiffElement<SpecDocumentChange, List<PostCoordinationSpecificationEvent>>> diffElements = revision2DiffElementsTranslator.getDiffElementsFromSpecRevision(eventsByView, createOrderAxisListWithSubAxis(), tableAxisLabels);
        List<DiffElement<CustomScaleDocumentChange, PostCoordinationCustomScalesValueEvent>> mutableDiffElements = new ArrayList<>(diffElements);
        mutableDiffElements.sort(Comparator.comparing(diffElement -> diffElement.getSourceDocument().getSortingCode()));

        List<DiffElement<String, String>> renderedDiffElements = renderDiffElementsForCustomScale(mutableDiffElements, renderedEntities);
        int pageElements = renderedDiffElements.size();
        int pageCount;
        if (pageElements == 0) {
            pageCount = 1;
        } else {
            pageCount = totalChanges / pageElements + (totalChanges % pageElements);
        }
        Page<DiffElement<String, String>> page = Page.create(
                1,
                pageCount,
                renderedDiffElements,
                totalChanges
        );
        ProjectChange projectChange = ProjectChange.get(
                RevisionNumber.valueOf("0"),
                revision.userId(),
                revision.timestamp(),
                "Edited Postcoordination Scale Values for Entity: " + subjectName,
                totalChanges,
                page);
        return projectChange;
    }
}
