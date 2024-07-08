package org.flickit.assessment.data.jpa.core.evidenceattachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvidenceAttachmentJpaRepository extends JpaRepository<EvidenceAttachmentJpaEntity, UUID> {

    int countByEvidenceId(UUID evidenceId);
}
