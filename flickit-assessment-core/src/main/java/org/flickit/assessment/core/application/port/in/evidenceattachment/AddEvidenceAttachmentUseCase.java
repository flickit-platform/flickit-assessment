package org.flickit.assessment.core.application.port.in.evidenceattachment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface AddEvidenceAttachmentUseCase {

    Result addAttachment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ADD_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL)
        UUID evidenceId;

        @NotNull(message = ADD_EVIDENCE_ATTACHMENT_ATTACHMENT_NOT_NULL)
        MultipartFile attachment;

        @Size(min = 3, message = ADD_EVIDENCE_ATTACHMENT_DESCRIPTION_SIZE_MIN)
        @Size(max = 100, message = ADD_EVIDENCE_ATTACHMENT_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID evidenceId, MultipartFile attachment, String description, UUID currentUserId) {
            this.evidenceId = evidenceId;
            this.attachment = (attachment != null && !attachment.isEmpty()) ? attachment : null;
            this.description = (description != null && !description.isBlank()) ? description.strip() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID attachmentId, String attachmentLink) {
    }
}
