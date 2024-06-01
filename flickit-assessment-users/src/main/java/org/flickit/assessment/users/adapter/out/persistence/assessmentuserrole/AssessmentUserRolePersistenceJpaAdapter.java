package org.flickit.assessment.users.adapter.out.persistence.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaRepository;
import org.flickit.assessment.users.application.port.out.assessmentuserrole.DeleteAssessmentUserRoleByUserIdPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentUserRolePersistenceJpaAdapter implements DeleteAssessmentUserRoleByUserIdPort {

    private final AssessmentUserRoleJpaRepository repository;

    @Override
    public void delete(UUID userId) {
        repository.deleteByUserId(userId);
    }
}
