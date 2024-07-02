package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.evidenceattachment.DeleteEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentFilePathPort;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaEntity;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ATTACHMENT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements
    DeleteEvidenceAttachmentPort,
    LoadEvidenceAttachmentFilePathPort {

    private final EvidenceAttachmentJpaRepository evidenceAttachmentRepository;

    @Override
    public void deleteEvidenceAttachment(UUID attachmentId) {
        if (evidenceAttachmentRepository.findById(attachmentId).isEmpty())
            throw new ResourceNotFoundException(EVIDENCE_ATTACHMENT_ID_NOT_FOUND);

        evidenceAttachmentRepository.deleteById(attachmentId);
    }

    @Override
    public String loadEvidenceAttachmentFilePath(UUID attachmentId) {
        EvidenceAttachmentJpaEntity entity = evidenceAttachmentRepository.findById(attachmentId)
            .orElseThrow(() -> new ResourceNotFoundException(EVIDENCE_ATTACHMENT_ID_NOT_FOUND));

        return entity.getFilePath();
    }
}
