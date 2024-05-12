package org.flickit.assessment.data.jpa.users.userassessmentrole;

import org.flickit.assessment.data.jpa.users.userassessmentrole.UserAssessmentRoleJpaEntity.EntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAssessmentRoleJpaRepository extends JpaRepository<UserAssessmentRoleJpaEntity, EntityId> {
}
