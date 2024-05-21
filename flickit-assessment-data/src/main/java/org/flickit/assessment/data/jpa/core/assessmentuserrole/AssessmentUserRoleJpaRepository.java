package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity.EntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentUserRoleJpaRepository extends JpaRepository<AssessmentUserRoleJpaEntity, EntityId> {
}
