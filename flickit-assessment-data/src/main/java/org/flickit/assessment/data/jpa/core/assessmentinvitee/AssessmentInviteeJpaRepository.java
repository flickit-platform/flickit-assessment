package org.flickit.assessment.data.jpa.core.assessmentinvitee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentInviteeJpaRepository extends JpaRepository<AssessmentInviteeJpaEntity, UUID> {

    Page<AssessmentInviteeJpaEntity> findByAssessmentId(UUID assessmentId, Pageable pageable);
}
