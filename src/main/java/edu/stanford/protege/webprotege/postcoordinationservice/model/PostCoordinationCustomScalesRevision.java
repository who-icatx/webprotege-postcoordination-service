package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.google.common.base.Objects;
import edu.stanford.protege.webprotege.common.ChangeRequestId;
import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.*;

import java.time.Instant;
import java.util.Set;

public record PostCoordinationCustomScalesRevision(UserId userId,
                                                   @Indexed(name = "rev_timestamp", direction = IndexDirection.DESCENDING) Long timestamp,
                                                   Set<PostCoordinationCustomScalesValueEvent> postCoordinationEvents,
                                                   CommitStatus commitStatus,
                                                   String changeRequestId) implements Comparable<PostCoordinationCustomScalesRevision> {


    public static PostCoordinationCustomScalesRevision create(UserId userId, Set<PostCoordinationCustomScalesValueEvent> postCoordinationEventList) {
        return create(userId, postCoordinationEventList, null);
    }

    public static PostCoordinationCustomScalesRevision create(UserId userId, Set<PostCoordinationCustomScalesValueEvent> postCoordinationEventList, ChangeRequestId changeRequestId) {
        CommitStatus status = changeRequestId != null && changeRequestId.id() != null ? CommitStatus.UNCOMMITTED : CommitStatus.COMMITTED;
        return new PostCoordinationCustomScalesRevision(userId, System.currentTimeMillis(), postCoordinationEventList, status, changeRequestId != null ? changeRequestId.id() : null);
    }

    public static PostCoordinationCustomScalesRevision createCommittedClone(PostCoordinationCustomScalesRevision revision) {
        return new PostCoordinationCustomScalesRevision(revision.userId(), revision.timestamp(), revision.postCoordinationEvents(), CommitStatus.COMMITTED, revision.changeRequestId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCoordinationCustomScalesRevision that = (PostCoordinationCustomScalesRevision) o;
        return Objects.equal(userId, that.userId) && Objects.equal(timestamp, that.timestamp) && Objects.equal(postCoordinationEvents, that.postCoordinationEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, timestamp, postCoordinationEvents);
    }

    @Override
    public int compareTo(@NotNull PostCoordinationCustomScalesRevision o) {
        return Long.compare(this.timestamp, o.timestamp);
    }

    @Override
    public String toString() {
        return "PostCoordinationCustomScalesRevision{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", postCoordinationEvents=" + postCoordinationEvents +
                '}';
    }
}
