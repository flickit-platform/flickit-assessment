package org.flickit.assessment.data.jpa.core.evidenceattachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EvidenceAttachmentJpaRepository extends JpaRepository<EvidenceAttachmentJpaEntity, UUID> {

    int countByEvidenceId(@Param("evidenceId") UUID evidenceId);
}
