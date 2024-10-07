package edu.stanford.protege.webprotege.postcoordinationservice.model;

import com.fasterxml.jackson.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

@Document(collection = PostcoordinationAxisToGenericScale.AXIS_TO_GENERIC_SCALE)
public class PostcoordinationAxisToGenericScale {

    @Field("postcoordinationAxis")
    private final String postcoordinationAxis;
    @Field("genericPostcoordinationScaleTopClass")
    private final String genericPostcoordinationScaleTopClass;

    @Field("allowMultiValue")
    private final String allowMultiValue;

    public final static String AXIS_TO_GENERIC_SCALE = "PostcoordinationAxisToGenericScale";

    @JsonCreator
    public PostcoordinationAxisToGenericScale(@JsonProperty("postcoordinationAxis") String postcoordinationAxis,
                                              @JsonProperty("genericPostcoordinationScaleTopClass") String genericPostcoordinationScaleTopClass,
                                              @JsonProperty("allowMultiValue") String allowMultiValue) {
        this.postcoordinationAxis = postcoordinationAxis;
        this.genericPostcoordinationScaleTopClass = genericPostcoordinationScaleTopClass;
        this.allowMultiValue = allowMultiValue;
    }


    @JsonProperty("postcoordinationAxis")
    public String getPostcoordinationAxis() {
        return postcoordinationAxis;
    }

    @JsonProperty("genericPostcoordinationScaleTopClass")
    public String getGenericPostcoordinationScaleTopClass() {
        return genericPostcoordinationScaleTopClass;
    }

    @JsonProperty("allowMultiValue")
    public String getAllowMultiValue() {
        return allowMultiValue;
    }
}
