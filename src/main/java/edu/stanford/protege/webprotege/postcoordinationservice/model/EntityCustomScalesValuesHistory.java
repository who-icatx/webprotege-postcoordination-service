package edu.stanford.protege.webprotege.postcoordinationservice.model;


import com.fasterxml.jackson.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.List;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.POSTCOORDINATION_CUSTOM_SCALES_COLLECTION;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = POSTCOORDINATION_CUSTOM_SCALES_COLLECTION)
public class EntityCustomScalesValuesHistory {

    public static final String POSTCOORDINATION_CUSTOM_SCALES_COLLECTION = "EntityPostCoordinationCustomScales";

    public static final String WHOFIC_ENTITY_IRI = "whoficEntityIri";
    public static final String PROJECT_ID = "projectId";
    public static final String CUSTOM_SCALE_REVISIONS = "postCoordinationCustomScalesRevisions";
    @Field(WHOFIC_ENTITY_IRI)
    @Indexed(name = "entityIriScales")
    private final String whoficEntityIri;

    @Field(PROJECT_ID)
    private final String projectId;

    @Field(CUSTOM_SCALE_REVISIONS)
    private final List<PostCoordinationCustomScalesRevision> postCoordinationCustomScalesRevisions;

    @JsonCreator
    public EntityCustomScalesValuesHistory(@JsonProperty(WHOFIC_ENTITY_IRI) String whoficEntityIri,
                                           @JsonProperty(PROJECT_ID) String projectId,
                                           @JsonProperty(CUSTOM_SCALE_REVISIONS) List<PostCoordinationCustomScalesRevision> postCoordinationCustomScalesRevisions) {
        this.whoficEntityIri = whoficEntityIri;
        this.projectId = projectId;
        this.postCoordinationCustomScalesRevisions = postCoordinationCustomScalesRevisions;
    }

    public static EntityCustomScalesValuesHistory create(String whoficEntityIri,
                                                         String projectId,
                                                         List<PostCoordinationCustomScalesRevision> postCoordinationCustomScalesRevisions) {
        return new EntityCustomScalesValuesHistory(whoficEntityIri, projectId, postCoordinationCustomScalesRevisions);
    }

    @JsonProperty(WHOFIC_ENTITY_IRI)
    public String getWhoficEntityIri() {
        return whoficEntityIri;
    }

    @JsonProperty(PROJECT_ID)
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty(CUSTOM_SCALE_REVISIONS)
    public List<PostCoordinationCustomScalesRevision> getPostCoordinationCustomScalesRevisions() {
        return postCoordinationCustomScalesRevisions;
    }
}
