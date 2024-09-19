package edu.stanford.protege.webprotege.postcoordinationservice.model;

import edu.stanford.protege.webprotege.postcoordinationservice.events.PostCoordinationEvent;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public record PostCoordinationViewEvent(@Field("linearizationView")
                                        String linearizationView,

                                        @Field("axisEvents")
                                        List<PostCoordinationEvent> axisEvents) {
}
