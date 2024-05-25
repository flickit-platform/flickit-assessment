package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import org.flickit.assessment.data.jpa.core.assessmentuserrole.AssessmentUserRoleJpaEntity.EntityId;
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

}
