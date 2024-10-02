package edu.stanford.protege.webprotege.postcoordinationservice.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.POSTCOORDINATION_CUSTOM_SCALES_COLLECTION;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = POSTCOORDINATION_CUSTOM_SCALES_COLLECTION)
public class EntityCustomScalesValuesHistory {

    public static final String POSTCOORDINATION_CUSTOM_SCALES_COLLECTION = "EntityPostCoordinationCustomScales";
    @Field("whoficEntityIri")
    @Indexed(name = "entityIriScales")
    private final String whoficEntityIri;

    @Field("projectId")
    private final String projectId;

    @Field("postCoordinationCustomScalesRevisions")
    private final List<PostCoordinationCustomScalesRevision> postCoordinationCustomScalesRevisions;

    @JsonCreator
    public EntityCustomScalesValuesHistory(@JsonProperty("whoficEntityIri") String whoficEntityIri,
                                           @JsonProperty("projectId") String projectId,
                                           @JsonProperty("postCoordinationCustomScalesRevisions") List<PostCoordinationCustomScalesRevision> postCoordinationCustomScalesRevisions) {
        this.whoficEntityIri = whoficEntityIri;
        this.projectId = projectId;
        this.postCoordinationCustomScalesRevisions = postCoordinationCustomScalesRevisions;
    }

    @JsonProperty("whoficEntityIri")
    public String getWhoficEntityIri() {
        return whoficEntityIri;
    }

    @JsonProperty("projectId")
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty("postCoordinationCustomScalesRevisions")
    public List<PostCoordinationCustomScalesRevision> getPostCoordinationCustomScalesRevisions() {
        return postCoordinationCustomScalesRevisions;
    }
}
