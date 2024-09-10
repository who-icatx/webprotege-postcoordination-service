package edu.stanford.protege.webprotege.postcoordinationservice.model;

import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationEvent;

import java.util.List;
import java.util.Set;

public class PostCoordinationRevision {
    public static final String LINEARIZATION_HISTORY_COLLECTION = "EntityPostCoordinationHistory";

    private final String userId;

    private final Long timestamp;

    private final Set<PostCoordinationEvent> postCoordinationEventList;


    public PostCoordinationRevision(String userId, Long timestamp, Set<PostCoordinationEvent> postCoordinationEventList) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.postCoordinationEventList = postCoordinationEventList;
    }

    public String getUserId() {
        return userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Set<PostCoordinationEvent> getPostCoordinationEventList() {
        return postCoordinationEventList;
    }
}
