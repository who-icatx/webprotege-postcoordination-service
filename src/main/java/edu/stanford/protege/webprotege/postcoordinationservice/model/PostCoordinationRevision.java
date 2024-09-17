package edu.stanford.protege.webprotege.postcoordinationservice.model;

import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationEvent;

import java.util.Set;

public record PostCoordinationRevision(String userId,
                                       Long timestamp,
                                       Set<PostCoordinationEvent> postCoordinationEventList) {

}
