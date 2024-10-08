package edu.stanford.protege.webprotege.postcoordinationservice.events;


import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.postcoordinationservice.model.WhoficCustomScalesValues;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes.CustomScaleChangeVisitor;
import edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.diff.ChangeOperationVisitorEx;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;

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
    }

    @Field("postCoordinationScaleValue")
    @JsonProperty("postCoordinationScaleValue")
    public String getPostCoordinationScaleValue() {
        return postCoordinationScaleValue;
    }

    public abstract <R> R accept(@Nonnull ChangeOperationVisitorEx<R> visitor);

    public abstract <R> R accept(@Nonnull CustomScaleChangeVisitor<R> visitor);
}
