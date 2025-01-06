package edu.stanford.protege.webprotege.postcoordinationservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LinearizationDefinition {


    @JsonProperty("Id")
    private final String id;

    @JsonProperty("whoficEntityIri")
    private final String whoficEntityIri;

    @JsonProperty("Description")
    private final String description;

    @JsonProperty("linearizationMode")
    private final String linearizationMode;

    @JsonProperty("rootId")
    private final String rootId;

    @JsonProperty("coreLinId")
    private final String coreLinId;

    @JsonProperty("sortingCode")
    private final String sortingCode;

    @JsonProperty("DisplayLabel")
    private final String displayLabel;

    @JsonProperty("oldId")
    private final String oldId;


    @JsonCreator
    public LinearizationDefinition(@JsonProperty("Id") String id,
                                   @JsonProperty("whoficEntityIri") String whoficEntityIri,
                                   @JsonProperty("oldId") String oldId,
                                   @JsonProperty("Description")String description,
                                   @JsonProperty("LinearizationMode") String linearizationMode,
                                   @JsonProperty("DisplayLabel") String displayLabel,
                                   @JsonProperty("rootId") String rootId,
                                   @JsonProperty("CoreLinId") String coreLinId,
                                   @JsonProperty("sortingCode") String sortingCode) {
        this.id = id;
        this.whoficEntityIri = whoficEntityIri;
        this.description = description;
        this.linearizationMode = linearizationMode;
        this.rootId = rootId;
        this.coreLinId = coreLinId;
        this.sortingCode = sortingCode;
        this.oldId = oldId;
        this.displayLabel = displayLabel;
    }

    public String getId() {
        return id;
    }

    public String getWhoficEntityIri() {
        return whoficEntityIri;
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
