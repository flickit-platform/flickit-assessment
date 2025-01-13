package org.flickit.assessment.core.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CountSpaceMembersPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.core.adapter.out.persistence.spaceuseraccess.SpaceUserAccessMapper.toJpaEntity;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;

@Component("coreSpaceUserAccessPersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements
    CheckSpaceAccessPort,
    CreateAssessmentSpaceUserAccessPort,
    CountSpaceMembersPort {

    private final SpaceUserAccessJpaRepository repository;
    private final AssessmentJpaRepository assessmentRepository;

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        return repository.existsBySpaceIdAndUserId(spaceId, userId);
    }

    @Override
    public void persist(CreateAssessmentSpaceUserAccessPort.Param param) {
        Long spaceId = assessmentRepository.findById(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND))
            .getSpaceId();
        repository.save(toJpaEntity(param, spaceId));
    }

    @Override
    public int countSpaceMembers(long spaceId) {
        return repository.countBySpaceId(spaceId);
    }
}
