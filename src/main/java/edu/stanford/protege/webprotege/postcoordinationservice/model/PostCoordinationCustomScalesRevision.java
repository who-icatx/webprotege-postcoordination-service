package edu.stanford.protege.webprotege.postcoordinationservice.model;

import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

public record PostCoordinationCustomScalesRevision(String userId,
                                                   @Indexed(name = "rev_timestamp", direction = IndexDirection.DESCENDING) Long timestamp,
                                                   Set<PostCoordinationCustomScalesValueEvent> postCoordinationEventList) {
}
