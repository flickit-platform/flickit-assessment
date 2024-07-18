package org.flickit.assessment.data.jpa.core.assessmentinvitee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AssessmentInviteeJpaRepository extends JpaRepository<AssessmentInviteeJpaEntity, UUID> {

    boolean existsByAssessmentIdAndEmail(UUID assessmentId, String email);

    @Modifying
    @Query("""
        UPDATE AssessmentInviteeJpaEntity a
        SET a.roleId = :roleId,
            a.expirationTime = :expirationTime,
            a.creationTime = :creationTime,
            a.createdBy = :createdBy
        WHERE a.assessmentId = :assessmentId AND a.email = :email
        """)
    void update(@Param("assessmentId") UUID assessmentId,
                @Param("email") String email,
                @Param("roleId") Integer roleId,
                @Param("creationTime") LocalDateTime creationTime,
                @Param("expirationTime") LocalDateTime expirationTime,
                @Param("createdBy") UUID createdBy);
}
