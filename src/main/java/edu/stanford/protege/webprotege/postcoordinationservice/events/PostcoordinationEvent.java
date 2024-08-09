
package edu.stanford.protege.webprotege.postcoordinationservice.events;


import com.fasterxml.jackson.annotation.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;


@BsonDiscriminator(key = "type")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME
)
@JsonSubTypes(value = {
})

public interface PostcoordinationEvent {

    @JsonProperty("@type")
    String getType();

    EventProcesableParameter applyEvent(EventProcesableParameter input);

    String getValue();
}
