package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.EvidenceAttachment;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaEntity;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentWithUserView;

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

    public static LoadEvidenceAttachmentsPort.Result mapToLoadPortResult(EvidenceAttachmentWithUserView entity) {
        return new LoadEvidenceAttachmentsPort.Result(
            entity.getId(),
            entity.getFilePath(),
            entity.getDescription(),
            new User(entity.getUserId(), entity.getDisplayName(), null),
            entity.getCreationTime());
    }
}
