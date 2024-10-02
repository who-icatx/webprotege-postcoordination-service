package edu.stanford.protege.webprotege.postcoordinationservice.events;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.springframework.data.mongodb.core.mapping.Field;

@BsonDiscriminator(key = "type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = AddCustomScaleValueEvent.class, name = AddCustomScaleValueEvent.TYPE),
        @JsonSubTypes.Type(value = RemoveCustomScaleValueEvent.class, name = RemoveCustomScaleValueEvent.TYPE),
})
public abstract class PostCoordinationCustomScalesValueEvent {

    @Field("postCoordinationAxis")
    private final String postCoordinationAxis;

    @Field("postCoordinationScaleValue")
    private final String postCoordinationScaleValue;

    protected PostCoordinationCustomScalesValueEvent(String postCoordinationAxis, String postCoordinationScaleValue) {
        this.postCoordinationAxis = postCoordinationAxis;
        this.postCoordinationScaleValue = postCoordinationScaleValue;
    }

    @JsonProperty("@type")
    abstract String getType();

    public abstract void applyEvent(WhoficCustomScalesValues whoficCustomScalesValues);

    @Field("postCoordinationAxis")
    @JsonProperty("postCoordinationAxis")
    public String getPostCoordinationAxis() {
        return postCoordinationAxis;
    };

    @Field("postCoordinationScaleValue")
    @JsonProperty("postCoordinationScaleValue")
    public String getPostCoordinationScaleValue() {
        return postCoordinationScaleValue;
    }
}
