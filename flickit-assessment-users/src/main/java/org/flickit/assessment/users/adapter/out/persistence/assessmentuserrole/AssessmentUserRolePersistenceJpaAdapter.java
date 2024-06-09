package org.flickit.assessment.users.adapter.out.persistence.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaRepository;
import org.flickit.assessment.users.application.port.out.assessmentuserrole.DeleteSpaceAssessmentUserRolesPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("userAssessmentUserRolePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentUserRolePersistenceJpaAdapter implements DeleteSpaceAssessmentUserRolesPort {

    private final AssessmentUserRoleJpaRepository repository;
    private final AssessmentJpaRepository assessmentJpaRepository;

    @Override
    public void delete(long spaceId, UUID userId) {
        List<AssessmentJpaEntity> assessmentEntities = assessmentJpaRepository.findBySpaceId(spaceId);
        List<UUID> assessmentIds = assessmentEntities.stream()
            .map(AssessmentJpaEntity::getId)
            .toList();
        repository.deleteByUserIdAndAssessmentIdIn(userId, assessmentIds);
    }
}
