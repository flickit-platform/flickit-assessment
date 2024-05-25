package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity.EntityId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AssessmentUserRoleJpaRepository extends JpaRepository<AssessmentUserRoleJpaEntity, EntityId> {

    Optional<AssessmentUserRoleJpaEntity> findByAssessmentIdAndUserId(UUID assessmentId, UUID currentUserId);

    boolean existsByAssessmentIdAndUserId(UUID assessmentId, UUID userId);

    @Modifying
    @Query("""
            UPDATE AssessmentUserRoleJpaEntity a SET
                a.roleId = :roleId
            WHERE a.assessmentId = :assessmentId AND a.userId = :userId
        """)
    void update(@Param("assessmentId") UUID assessmentId, @Param("userId") UUID userId, @Param("roleId") int roleId);

    void deleteByAssessmentIdAndUserId(UUID assessmentId, UUID userId);

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
    Page<AssessmentPrivilegedUserView> findAssessmentPrivilegedUsers(@Param("assessmentId") UUID assessmentId,
                                                                     Pageable pageable);
}
