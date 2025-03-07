package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.model.PostCoordinationSpecificationRevision;
import org.springframework.data.mongodb.core.mapping.Field;

import static edu.stanford.protege.webprotege.postcoordinationservice.model.EntityPostCoordinationHistory.*;

public record SpecRevisionWithEntity(@Field(SPEC_REVISIONS) PostCoordinationSpecificationRevision revision,
                                     ProjectId projectId, @Field(WHOFIC_ENTITY_IRI) String whoficEntityIri) {

    @Override
    public PostCoordinationSpecificationRevision revision() {
        return revision;
    }

    @Override
    public String whoficEntityIri() {
        return whoficEntityIri;
    }
}
