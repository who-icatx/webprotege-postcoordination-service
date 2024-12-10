package edu.stanford.protege.webprotege.postcoordinationservice.model;


import com.google.common.base.Objects;
import edu.stanford.protege.webprotege.common.ChangeRequestId;
import edu.stanford.protege.webprotege.common.UserId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.*;

import java.time.Instant;
import java.util.Set;

public record PostCoordinationSpecificationRevision(UserId userId,
                                                    @Indexed(name = "spec_timestamp", direction = IndexDirection.DESCENDING) Long timestamp,
                                                    Set<PostCoordinationViewEvent> postCoordinationEvents,
                                                    CommitStatus commitStatus,
                                                    String changeRequestId) implements Comparable<PostCoordinationSpecificationRevision>{


    public static PostCoordinationSpecificationRevision create(UserId userId, Set<PostCoordinationViewEvent> postCoordinationEventList) {
        return create(userId, postCoordinationEventList, null);
    }
    public static PostCoordinationSpecificationRevision create(UserId userId, Set<PostCoordinationViewEvent> postCoordinationEventList, ChangeRequestId changeRequestId) {
        CommitStatus status = changeRequestId != null && changeRequestId.id() != null ? CommitStatus.UNCOMMITTED : CommitStatus.COMMITTED;
        return new PostCoordinationSpecificationRevision(userId, Instant.now().toEpochMilli(), postCoordinationEventList, status, changeRequestId != null ? changeRequestId.id() : null);
    }

    public static PostCoordinationSpecificationRevision createCommittedClone(PostCoordinationSpecificationRevision revision) {
        return new PostCoordinationSpecificationRevision(revision.userId, revision.timestamp, revision.postCoordinationEvents, CommitStatus.COMMITTED, revision.changeRequestId);
    }

    @Override
    public int compareTo(@NotNull PostCoordinationSpecificationRevision o) {
        return Long.compare(this.timestamp, o.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCoordinationSpecificationRevision that = (PostCoordinationSpecificationRevision) o;
        return Objects.equal(userId, that.userId) && Objects.equal(timestamp, that.timestamp) && Objects.equal(postCoordinationEvents, that.postCoordinationEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, timestamp, postCoordinationEvents);
    }

    @Override
    public String toString() {
        return "PostCoordinationSpecificationRevision{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", postCoordinationEvents=" + postCoordinationEvents +
                '}';
    }
}
