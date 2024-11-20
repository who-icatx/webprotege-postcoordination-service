package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

import edu.stanford.protege.webprotege.change.ProjectChange;
import org.jetbrains.annotations.NotNull;

public record ProjectChangeForEntity(String whoficEntityIri,
                                     ChangeType changeType,
                                     ProjectChange projectChange) implements Comparable<ProjectChangeForEntity> {

    public static ProjectChangeForEntity create(String whoficEntityIri,
                                                ChangeType changeType,
                                                ProjectChange projectChange) {
        return new ProjectChangeForEntity(whoficEntityIri, changeType, projectChange);
    }

    //All linearization/postcoordination changes are updates made on the whoficEntityIri.
    // From this microservice we don't create or delete entities. that is the responsibility of the backend-service.
    public static ProjectChangeForEntity create(String whoficEntityIri,
                                                ProjectChange projectChange) {
        return new ProjectChangeForEntity(whoficEntityIri, ChangeType.UPDATE_ENTITY, projectChange);
    }

    @Override
    public int compareTo(@NotNull ProjectChangeForEntity other) {
        int timestampComparison = Long.compare(this.projectChange.getTimestamp(), other.projectChange.getTimestamp());
        if (timestampComparison != 0) {
            return timestampComparison;
        }
        return this.whoficEntityIri.compareTo(other.whoficEntityIri);
    }
}
