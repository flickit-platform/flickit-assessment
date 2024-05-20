package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity.EntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentUserRoleJpaRepository extends JpaRepository<AssessmentUserRoleJpaEntity, EntityId> {

    Optional<AssessmentUserRoleJpaEntity> findByAssessmentIdAndUserId(UUID assessmentId, UUID currentUserId);

    @Query("""
        SELECT
            u.id AS userId,
            u.email AS email,
            u.displayName AS displayName,
            u.bio AS bio,
            u.picture AS picturePath,
            u.linkedin AS linkedin,
            a.roleId AS roleId
        FROM UserJpaEntity u JOIN AssessmentUserRoleJpaEntity a ON u.id = a.userId
        WHERE a.assessmentId = :assessmentId
    """)
    Page<AssessmentPrivilegedUserView> findAssessmentPrivilegedUsers(UUID assessmentId, Pageable pageable);
}
