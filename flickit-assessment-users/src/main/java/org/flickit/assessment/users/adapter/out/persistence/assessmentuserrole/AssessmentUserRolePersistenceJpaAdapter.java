package org.flickit.assessment.users.adapter.out.persistence.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaRepository;
import org.flickit.assessment.users.application.port.out.assessmentuserrole.DeleteSpaceAssessmentUserRolesPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("userAssessmentUserRolePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentUserRolePersistenceJpaAdapter implements DeleteSpaceAssessmentUserRolesPort {

    private final AssessmentUserRoleJpaRepository repository;

    @Override
    public void delete(UUID userId, long spaceId) {
        repository.deleteByUserIdAndSpaceId(userId, spaceId);
    }
}
