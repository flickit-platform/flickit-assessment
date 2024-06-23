package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.evidenceattachment.SaveEvidenceAttachmentPort;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaEntity;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements SaveEvidenceAttachmentPort {

    EvidenceAttachmentJpaRepository repository;

    @Override
    public UUID saveAttachment(UUID evidenceId, String filePath, UUID currentUserId, LocalDateTime now) {
        var unsavedEntity = new EvidenceAttachmentJpaEntity(null, evidenceId, filePath, currentUserId, now);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }
}
