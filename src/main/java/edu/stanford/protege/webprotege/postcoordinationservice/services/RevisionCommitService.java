package edu.stanford.protege.webprotege.postcoordinationservice.services;

import edu.stanford.protege.webprotege.common.ChangeRequestId;
import edu.stanford.protege.webprotege.common.ProjectId;
import edu.stanford.protege.webprotege.postcoordinationservice.model.*;
import edu.stanford.protege.webprotege.postcoordinationservice.repositories.PostCoordinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class RevisionCommitService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RevisionCommitService.class);
    private final PostCoordinationRepository postCoordinationRepository;

    public RevisionCommitService(PostCoordinationRepository postCoordinationRepository) {
        this.postCoordinationRepository = postCoordinationRepository;
    }

    @Transactional
    public void rollbackRevision(ChangeRequestId changeRequestId, ProjectId projectId, String entityIri) {
        postCoordinationRepository.deletePostCoordinationCustomScalesRevision(changeRequestId, projectId, entityIri);
        postCoordinationRepository.deletePostCoordinationSpecificationRevision(changeRequestId, projectId, entityIri);
    }


    public void commitRevision(ChangeRequestId changeRequestId, ProjectId projectId, String entityIri) {
        postCoordinationRepository.commitPostCoordinationSpecificationRevision(changeRequestId, projectId, entityIri);
        postCoordinationRepository.commitPostCoordinationCustomScalesRevision(changeRequestId, projectId, entityIri);
    }
}
