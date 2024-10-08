package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.nodeRendering;

import com.fasterxml.jackson.annotation.*;
import edu.stanford.protege.webprotege.dispatch.Result;
import edu.stanford.protege.webprotege.entity.EntityNode;

import java.util.List;

@JsonTypeName(GetRenderedOwlEntitiesAction.CHANNEL)
public record GetRenderedOwlEntitiesResult(@JsonProperty List<EntityNode> renderedEntities) implements Result {

}
