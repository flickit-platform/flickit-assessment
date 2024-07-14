package org.flickit.assessment.data.jpa.core.assessmentinvitee;

import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentInviteeJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {
}
