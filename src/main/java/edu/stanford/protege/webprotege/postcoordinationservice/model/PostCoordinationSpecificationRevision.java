package edu.stanford.protege.webprotege.postcoordinationservice.model;


import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

public record PostCoordinationSpecificationRevision(String userId,
                                                    @Indexed(name = "spec_timestamp", direction = IndexDirection.DESCENDING) Long timestamp,
                                                    Set<PostCoordinationViewEvent> postCoordinationEventList) {

}
