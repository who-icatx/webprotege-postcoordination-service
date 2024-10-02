
package edu.stanford.protege.webprotege.postcoordinationservice.events;


import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.PostCoordinationSpecification;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.springframework.data.mongodb.core.mapping.Field;


@BsonDiscriminator(key = "type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = AddToRequiredAxisEvent.class, name = AddToRequiredAxisEvent.TYPE),
        @JsonSubTypes.Type(value = AddToDefaultAxisEvent.class, name = AddToDefaultAxisEvent.TYPE),
        @JsonSubTypes.Type(value = AddToNotAllowedAxisEvent.class, name = AddToNotAllowedAxisEvent.TYPE),
        @JsonSubTypes.Type(value = AddToAllowedAxisEvent.class, name = AddToAllowedAxisEvent.TYPE)
})
public abstract class PostCoordinationSpecificationEvent {

    @Field("postCoordinationAxis")
    private final String postCoordinationAxis;

    @Field("linearizationView")
    private final String linearizationView;

    protected PostCoordinationSpecificationEvent(String postCoordinationAxis, String linearizationView) {
        this.postCoordinationAxis = postCoordinationAxis;
        this.linearizationView = linearizationView;
    }

    @JsonProperty("@type")
    abstract String getType();

    public PostCoordinationSpecification applyEvent(PostCoordinationSpecification input) {
        input.getAllowedAxes().remove(this.postCoordinationAxis);
        input.getDefaultAxes().remove(this.postCoordinationAxis);
        input.getNotAllowedAxes().remove(this.postCoordinationAxis);
        input.getRequiredAxes().remove(this.postCoordinationAxis);
        return applySpecificEvent(input);
    }

    abstract PostCoordinationSpecification applySpecificEvent(PostCoordinationSpecification input);


    @Field("postCoordinationAxis")
    @JsonProperty("postCoordinationAxis")
    public String getPostCoordinationAxis() {
        return postCoordinationAxis;
    };
    @Field("linearizationView")
    @JsonProperty("linearizationView")
    public String getLinearizationView() {
        return linearizationView;
    }
}
