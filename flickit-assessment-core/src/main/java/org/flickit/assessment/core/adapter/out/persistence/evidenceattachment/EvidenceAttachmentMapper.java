package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.EvidenceAttachment;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort.Result;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceAttachmentMapper {

    public static EvidenceAttachmentJpaEntity mapToJpaEntity(EvidenceAttachment attachment) {
        return new EvidenceAttachmentJpaEntity(
            attachment.getId(),
            attachment.getEvidenceId(),
            attachment.getFilePath(),
            attachment.getDescription(),
            attachment.getCreatedBy(),
            attachment.getCreationTime());
    }

    public static LoadEvidenceAttachmentsPort.Result mapToPortResult(EvidenceAttachmentJpaEntity evidenceAttachment) {
        return new LoadEvidenceAttachmentsPort.Result(evidenceAttachment.getId(),
            evidenceAttachment.getFilePath(),
            evidenceAttachment.getDescription(),
            evidenceAttachment.getCreatedBy());
    }
}
