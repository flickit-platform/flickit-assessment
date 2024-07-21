package org.flickit.assessment.data.jpa.core.assessmentinvitee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssessmentInviteeJpaRepository extends JpaRepository<AssessmentInviteeJpaEntity, UUID> {

    List<AssessmentInviteeJpaEntity> findAllByEmail(String email);

    void deleteByEmail(String email);
}
