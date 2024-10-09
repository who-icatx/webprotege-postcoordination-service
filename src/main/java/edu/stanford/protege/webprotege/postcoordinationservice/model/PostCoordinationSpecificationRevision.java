package edu.stanford.protege.webprotege.postcoordinationservice.model;


import com.google.common.base.Objects;
import edu.stanford.protege.webprotege.common.UserId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.*;

import java.time.Instant;
import java.util.Set;

public record PostCoordinationSpecificationRevision(UserId userId,
                                                    @Indexed(name = "spec_timestamp", direction = IndexDirection.DESCENDING) Long timestamp,
                                                    Set<PostCoordinationViewEvent> postCoordinationEventList) implements Comparable<PostCoordinationSpecificationRevision>{


    private static long lastTimestamp = 0;
    private static int counter = 0;

    public static PostCoordinationSpecificationRevision create(UserId userId, Set<PostCoordinationViewEvent> postCoordinationEventList) {
        long currentTimestamp = Instant.now().toEpochMilli();
        if (currentTimestamp == lastTimestamp) {
            counter++;
        } else {
            lastTimestamp = currentTimestamp;
            counter = 0;
        }
        return new PostCoordinationSpecificationRevision(userId, currentTimestamp + counter, postCoordinationEventList);
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
        return Objects.equal(userId, that.userId) && Objects.equal(timestamp, that.timestamp) && Objects.equal(postCoordinationEventList, that.postCoordinationEventList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, timestamp, postCoordinationEventList);
    }

    @Override
    public String toString() {
        return "PostCoordinationSpecificationRevision{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", postCoordinationEventList=" + postCoordinationEventList +
                '}';
    }
}
