package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LinearizationDefinition {


    @JsonProperty("linearizationId")
    private final String linearizationId;

    @JsonProperty("linearizationUri")
    private final String linearizationUri;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("linearizationMode")
    private final String linearizationMode;

    @JsonProperty("rootId")
    private final String rootId;

    @JsonProperty("coreLinId")
    private final String coreLinId;

    @JsonProperty("sortingCode")
    private final String sortingCode;

    @JsonProperty("displayLabel")
    private final String displayLabel;

    @JsonProperty("oldId")
    private final String oldId;


    @JsonCreator
    public LinearizationDefinition(@JsonProperty("linearizationId") String linearizationId,
                                   @JsonProperty("linearizationUri") String linearizationUri,
                                   @JsonProperty("oldId") String oldId,
                                   @JsonProperty("description")String description,
                                   @JsonProperty("linearizationMode") String linearizationMode,
                                   @JsonProperty("displayLabel") String displayLabel,
                                   @JsonProperty("rootId") String rootId,
                                   @JsonProperty("CoreLinId") String coreLinId,
                                   @JsonProperty("sortingCode") String sortingCode) {
        this.linearizationId = linearizationId;
        this.linearizationUri = linearizationUri;
        this.description = description;
        this.linearizationMode = linearizationMode;
        this.rootId = rootId;
        this.coreLinId = coreLinId;
        this.sortingCode = sortingCode;
        this.oldId = oldId;
        this.displayLabel = displayLabel;
    }

    public String getLinearizationId() {
        return linearizationId;
    }

    public String getLinearizationUri() {
        return linearizationUri;
    }

    public String getLinearizationMode() {
        return linearizationMode;
    }

    public String getRootId() {
        return rootId;
    }

    public String getCoreLinId() {
        return coreLinId;
    }

    public String getSortingCode() {
        return sortingCode;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getOldId() {
        return oldId;
    }
}
