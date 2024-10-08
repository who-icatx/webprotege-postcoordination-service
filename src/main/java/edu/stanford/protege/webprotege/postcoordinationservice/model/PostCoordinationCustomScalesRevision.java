package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.google.common.base.Objects;
import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationCustomScalesValueEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.*;

import java.time.Instant;
import java.util.Set;

public record PostCoordinationCustomScalesRevision(UserId userId,
                                                   @Indexed(name = "rev_timestamp", direction = IndexDirection.DESCENDING) Long timestamp,
                                                   Set<PostCoordinationCustomScalesValueEvent> postCoordinationEventList) implements Comparable<PostCoordinationCustomScalesRevision> {

    private static long lastTimestamp = 0;
    private static int counter = 0;

    public static PostCoordinationCustomScalesRevision create(UserId userId, Set<PostCoordinationCustomScalesValueEvent> postCoordinationEventList) {
        long currentTimestamp = Instant.now().toEpochMilli();
        if (currentTimestamp == lastTimestamp) {
            counter++;
        } else {
            lastTimestamp = currentTimestamp;
            counter = 0;
        }
        return new PostCoordinationCustomScalesRevision(userId, currentTimestamp + counter, postCoordinationEventList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCoordinationCustomScalesRevision that = (PostCoordinationCustomScalesRevision) o;
        return Objects.equal(userId, that.userId) && Objects.equal(timestamp, that.timestamp) && Objects.equal(postCoordinationEventList, that.postCoordinationEventList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, timestamp, postCoordinationEventList);
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
                ", postCoordinationEventList=" + postCoordinationEventList +
                '}';
    }
}
