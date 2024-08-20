package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class CompositeAxis {

    @Field("postcoordinationAxis")
    private final String postCoordinationAxis;

    @Field("replacedBySubaxes")
    private final List<String> subAxis;

    @JsonCreator
    public CompositeAxis(@JsonProperty("postcoordinationAxis") String postCoordinationAxis, @JsonProperty("replacedBySubaxes") List<String> subAxis) {
        this.postCoordinationAxis = postCoordinationAxis;
        this.subAxis = subAxis;
    }

    @JsonProperty("postcoordinationAxis")
    public String getPostCoordinationAxis() {
        return postCoordinationAxis;
    }

    @JsonProperty("replacedBySubaxes")
    public List<String> getSubAxis() {
        return subAxis;
    }
}
