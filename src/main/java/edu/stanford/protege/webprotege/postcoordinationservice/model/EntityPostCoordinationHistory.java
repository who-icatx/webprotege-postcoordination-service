package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = EntityPostCoordinationHistory.POSTCOORDINATION_HISTORY_COLLECTION)
public class EntityPostCoordinationHistory {


    public static final String POSTCOORDINATION_HISTORY_COLLECTION = "EntityPostCoordinationHistory";

    public static final String WHOFIC_ENTITY_IRI = "whoficEntityIri";
    public static final String PROJECT_ID = "projectId";
    public static final String SPEC_REVISIONS = "postCoordinationRevisions";

    @Field(WHOFIC_ENTITY_IRI)
    @Indexed(name = "entityIriSpec")
    private final String whoficEntityIri;

    @Field(PROJECT_ID)
    private final String projectId;

    @Field(SPEC_REVISIONS)
    private final List<PostCoordinationSpecificationRevision> postCoordinationRevisions;


    @JsonCreator
    public EntityPostCoordinationHistory(@JsonProperty(WHOFIC_ENTITY_IRI) String whoficEntityIri,
                                         @JsonProperty(PROJECT_ID) String projectId,
                                         @JsonProperty(SPEC_REVISIONS) List<PostCoordinationSpecificationRevision> postCoordinationRevisions) {
        this.whoficEntityIri = whoficEntityIri;
        this.projectId = projectId;
        this.postCoordinationRevisions = postCoordinationRevisions;
    }

    @JsonProperty(WHOFIC_ENTITY_IRI)
    public String getWhoficEntityIri() {
        return whoficEntityIri;
    }

    @JsonProperty(PROJECT_ID)
    public String getProjectId() {
        return projectId;
    }

    @JsonProperty(SPEC_REVISIONS)
    public List<PostCoordinationSpecificationRevision> getPostCoordinationRevisions() {
        return postCoordinationRevisions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityPostCoordinationHistory that = (EntityPostCoordinationHistory) o;
        return Objects.equals(whoficEntityIri, that.whoficEntityIri) && Objects.equals(projectId, that.projectId) && Objects.equals(postCoordinationRevisions, that.postCoordinationRevisions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whoficEntityIri, projectId, postCoordinationRevisions);
    }

    @Override
    public String toString() {
        return "EntityPostCoordinationHistory{" +
                "whoficEntityIri='" + whoficEntityIri + '\'' +
                ", projectId='" + projectId + '\'' +
                ", postCoordinationRevisions=" + postCoordinationRevisions +
                '}';
    }
}
