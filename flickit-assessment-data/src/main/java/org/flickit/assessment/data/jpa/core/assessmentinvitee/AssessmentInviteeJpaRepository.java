package org.flickit.assessment.data.jpa.core.assessmentinvitee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessmentInviteeJpaRepository extends JpaRepository<AssessmentInviteeJpaEntity, UUID> {

    Optional<AssessmentInviteeJpaEntity> findByAssessmentIdAndEmail(UUID uuid, String email);
}
