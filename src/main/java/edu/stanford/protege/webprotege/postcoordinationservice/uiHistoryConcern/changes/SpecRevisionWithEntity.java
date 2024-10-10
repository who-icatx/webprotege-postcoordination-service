package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationSpecificationRevision;
import org.springframework.data.mongodb.core.mapping.Field;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.*;

public class SpecRevisionWithEntity {

    @Field(SPEC_REVISIONS)
    private final PostCoordinationSpecificationRevision revision;
    @Field(WHOFIC_ENTITY_IRI)
    private final String whoficEntityIri;
    public SpecRevisionWithEntity(PostCoordinationSpecificationRevision revision, String whoficEntityIri) {
        this.revision = revision;
        this.whoficEntityIri = whoficEntityIri;
    }

    public PostCoordinationSpecificationRevision getRevision() {
        return revision;
    }

    public String getWhoficEntityIri() {
        return whoficEntityIri;
    }
}
