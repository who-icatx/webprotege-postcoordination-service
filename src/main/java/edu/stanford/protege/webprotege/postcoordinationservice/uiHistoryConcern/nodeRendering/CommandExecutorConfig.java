package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.nodeRendering;

import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.ipc.impl.CommandExecutorImpl;
import edu.stanford.protege.webprotege.renderer.*;
import org.springframework.context.annotation.*;

@Configuration
public class CommandExecutorConfig {

    @Bean
    public CommandExecutor<GetRenderedOwlEntitiesAction, GetRenderedOwlEntitiesResult> getRenderedOwlEntitiesExecutor() {
        return new CommandExecutorImpl<>(GetRenderedOwlEntitiesResult.class);
    }

    @Bean
    public CommandExecutor<GetEntityHtmlRenderingAction, GetEntityHtmlRenderingResult> getRenderedEntityExecutor() {
        return new CommandExecutorImpl<>(GetEntityHtmlRenderingResult.class);
    }

}
