package edu.stanford.protege.webprotege.postcoordinationservice.model;


import com.google.common.base.Objects;
import edu.stanford.protege.webprotege.common.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.events.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.*;

import java.util.*;
import java.util.stream.Collectors;

public record PostCoordinationSpecificationRevision(UserId userId,
                                                    @Indexed(name = "spec_timestamp", direction = IndexDirection.DESCENDING) Long timestamp,
                                                    Set<PostCoordinationViewEvent> postCoordinationEvents,
                                                    CommitStatus commitStatus,
                                                    String changeRequestId) implements Comparable<PostCoordinationSpecificationRevision> {


    public static PostCoordinationSpecificationRevision create(UserId userId, Set<PostCoordinationViewEvent> postCoordinationEventList) {
        return create(userId, postCoordinationEventList, null);
    }

    public static PostCoordinationSpecificationRevision create(UserId userId, Set<PostCoordinationViewEvent> postCoordinationEventList, ChangeRequestId changeRequestId) {
        CommitStatus status = changeRequestId != null && changeRequestId.id() != null ? CommitStatus.UNCOMMITTED : CommitStatus.COMMITTED;
        return new PostCoordinationSpecificationRevision(userId, System.currentTimeMillis(), postCoordinationEventList, status, changeRequestId != null ? changeRequestId.id() : null);
    }


    public static PostCoordinationSpecificationRevision createDefaultInitialRevision(List<String> entityTypes,
                                                                                     List<LinearizationDefinition> definitionList,
                                                                                     List<TableConfiguration> configurations) {
        Set<PostCoordinationViewEvent> postCoordinationEvents = new HashSet<>();


        for (LinearizationDefinition definition : definitionList) {
            Set<String> postCoordinationAxis  = configurations.stream()
                    .filter(config -> entityTypes.contains(config.getEntityType()))
                    .flatMap(config -> config.getPostCoordinationAxes().stream())
                    .collect(Collectors.toSet());
            if(postCoordinationAxis.isEmpty()) {
                throw  new RuntimeException("Couldn't find the equivalent entity type " + entityTypes);
            }

            List<PostCoordinationSpecificationEvent> specificationEvents = postCoordinationAxis.stream()
                    .map(availableAxis -> {
                        if (definition.getCoreLinId() != null && !definition.getCoreLinId().isEmpty()) {
                            return new AddToDefaultAxisEvent(availableAxis, definition.getLinearizationUri());
                        } else {
                            return new AddToNotAllowedAxisEvent(availableAxis, definition.getLinearizationUri());
                        }
                    }).toList();
            postCoordinationEvents.add(new PostCoordinationViewEvent(definition.getLinearizationUri(), specificationEvents));
        }

        return new PostCoordinationSpecificationRevision(UserId.valueOf("initialRevision"),
                new Date().getTime(),
                postCoordinationEvents,
                CommitStatus.COMMITTED,
                null
        );
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
