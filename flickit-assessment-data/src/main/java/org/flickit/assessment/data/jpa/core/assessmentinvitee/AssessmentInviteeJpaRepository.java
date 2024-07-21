package org.flickit.assessment.data.jpa.core.assessmentinvitee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentInviteeJpaRepository extends JpaRepository<AssessmentInviteeJpaEntity, UUID> {

    Page<AssessmentInviteeJpaEntity> findByAssessmentId(UUID assessmentId, Pageable pageable);

    Optional<AssessmentInviteeJpaEntity> findByAssessmentIdAndEmail(UUID uuid, String email);

    List<AssessmentInviteeJpaEntity> findAllByEmail(String email);

    void deleteByEmail(String email);
}
