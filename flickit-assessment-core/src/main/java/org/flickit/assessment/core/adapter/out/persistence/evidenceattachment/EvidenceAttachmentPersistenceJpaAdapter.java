package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.EvidenceAttachment;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CountEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.CreateEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaRepository;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements
    CreateEvidenceAttachmentPort,
    CountEvidenceAttachmentsPort,
    LoadEvidenceAttachmentsPort {

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

    @Override
    public List<Result> loadEvidenceAttachments(UUID evidenceId) {
        if (!evidenceRepository.existsById(evidenceId))
            throw new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND);

        var jpaEntityList = repository.findByEvidenceId(evidenceId);
        return jpaEntityList
            .stream()
            .map(EvidenceAttachmentMapper::mapToPortResult).toList();
    }
}
