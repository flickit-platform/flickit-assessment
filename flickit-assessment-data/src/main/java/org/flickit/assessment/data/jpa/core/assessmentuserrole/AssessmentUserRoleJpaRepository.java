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
                u.picture AS picturePath,
                a.roleId as roleId,
                CASE
                    WHEN sp.ownerId = u.id THEN false ELSE true
                END as editable
            FROM UserJpaEntity u
            JOIN AssessmentUserRoleJpaEntity a ON u.id = a.userId
            JOIN AssessmentJpaEntity assessment ON assessment.id = a.assessmentId
            JOIN SpaceJpaEntity sp ON assessment.spaceId = sp.id
            WHERE a.assessmentId = :assessmentId
            AND EXISTS (
                  SELECT 1 FROM SpaceUserAccessJpaEntity sua
                  LEFT JOIN AssessmentJpaEntity fa on fa.spaceId = sua.spaceId
                  WHERE fa.id = :assessmentId AND sua.userId  = a.userId
                )
        """)
    Page<AssessmentUserView> findAssessmentUsers(@Param("assessmentId") UUID assessmentId, Pageable pageable);

    @Modifying
    @Query("""
            DELETE FROM AssessmentUserRoleJpaEntity r
            WHERE r.userId = :userId AND r.assessmentId IN
                (SELECT a.id FROM AssessmentJpaEntity a WHERE a.spaceId = :spaceId)
        """)
    void deleteByUserIdAndSpaceId(@Param("userId") UUID userId, @Param("spaceId") Long spaceId);
}
