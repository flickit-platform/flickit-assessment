package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.EvidenceAttachment;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CountEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CreateEvidenceAttachmentPort;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaRepository;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements
    CreateEvidenceAttachmentPort,
    CountEvidenceAttachmentsPort {

    private final EvidenceAttachmentJpaRepository repository;
    private final EvidenceJpaRepository evidenceRepository;

    @Override
    public UUID persist(EvidenceAttachment attachment) {
        if (!evidenceRepository.existsByIdAndDeletedFalse(attachment.getEvidenceId()))
            throw new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND);
        var unsavedEntity = EvidenceAttachmentMapper.mapToJpaEntity(attachment);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public int countAttachments(UUID evidenceId) {
        return repository.countByEvidenceId(evidenceId);
    }
}
