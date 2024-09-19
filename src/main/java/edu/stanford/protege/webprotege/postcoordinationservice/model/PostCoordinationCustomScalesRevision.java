package edu.stanford.protege.webprotege.postcoordinationservice.model;

import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;

import java.util.Set;

public record PostCoordinationCustomScalesRevision(String userId,
                                                   Long timestamp,
                                                   Set<PostCoordinationCustomScalesValueEvent> postCoordinationEventList) {
}
