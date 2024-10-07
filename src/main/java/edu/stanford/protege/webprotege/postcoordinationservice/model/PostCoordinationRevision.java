package edu.stanford.protege.webprotege.postcoordinationservice.model;


import java.util.Set;

public record PostCoordinationRevision(String userId,
                                       Long timestamp,
                                       Set<PostCoordinationViewEvent> postCoordinationEventList) {

}
