package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CountEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CreateEvidenceAttachmentPort;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaEntity;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements CreateEvidenceAttachmentPort, CountEvidenceAttachmentsPort {

    private final EvidenceAttachmentJpaRepository repository;

    @Override
    public UUID persist(UUID evidenceId, String filePath, String description, UUID currentUserId, LocalDateTime now) {
        var unsavedEntity = new EvidenceAttachmentJpaEntity(null, evidenceId, filePath, description, currentUserId, now);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public int countAttachments(UUID evidenceId) {
        return repository.countByEvidenceId(evidenceId);
    }
}
