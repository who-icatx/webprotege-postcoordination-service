package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationCustomScalesRevision;
import org.springframework.data.mongodb.core.mapping.Field;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityCustomScalesValuesHistory.*;

public class CustomScaleRevisionWithEntity {
    @Field(CUSTOM_SCALE_REVISIONS)
    private final PostCoordinationCustomScalesRevision revision;
    @Field(WHOFIC_ENTITY_IRI)
    private final String whoficEntityIri;

    public CustomScaleRevisionWithEntity(PostCoordinationCustomScalesRevision revision,
                                         String whoficEntityIri) {
        this.revision = revision;
        this.whoficEntityIri = whoficEntityIri;
    }

    public PostCoordinationCustomScalesRevision getRevision() {
        return revision;
    }

    public String getWhoficEntityIri() {
        return whoficEntityIri;
    }
}

