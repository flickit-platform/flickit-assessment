package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CountEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CreateEvidenceAttachmentPort;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaEntity;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements CreateEvidenceAttachmentPort, CountEvidenceAttachmentsPort {

    private final EvidenceAttachmentJpaRepository repository;

    @Override
    public UUID persist(UUID evidenceId, String filePath, String description, UUID currentUserId, LocalDateTime now) {
        if (repository.existsById(evidenceId))
            throw new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND);
        var unsavedEntity = new EvidenceAttachmentJpaEntity(null, evidenceId, filePath, description, currentUserId, now);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public int countAttachments(UUID evidenceId) {
        return repository.countByEvidenceId(evidenceId);
    }
}
