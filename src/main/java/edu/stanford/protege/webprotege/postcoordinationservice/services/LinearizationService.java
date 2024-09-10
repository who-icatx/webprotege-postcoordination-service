package edu.stanford.protege.webprotege.postcoordinationservice.services;


import edu.stanford.protege.webprotege.ipc.CommandExecutor;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinition;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinitionRequest;
import edu.stanford.protege.webprotege.postcoordinationservice.dto.LinearizationDefinitionResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinearizationService {


    private final CommandExecutor<LinearizationDefinitionRequest, LinearizationDefinitionResponse> commandExecutor;

    public LinearizationService(CommandExecutor<LinearizationDefinitionRequest, LinearizationDefinitionResponse> commandExecutor) {
        this.commandExecutor = commandExecutor;
    }


    @Cacheable("linearizationDefinitions")
    public List<LinearizationDefinition> getLinearizationDefinitions() {
        try {
            return commandExecutor.execute(new LinearizationDefinitionRequest(), new ExecutionContext()).get().definitionList();
        } catch (Exception e) {
            throw new RuntimeException("Exception fetching the definitions ", e);
        }
    }
}
