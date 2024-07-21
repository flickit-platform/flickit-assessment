package org.flickit.assessment.core.application.port.in.evidenceattachment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_ATTACHMENT_ATTACHMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteEvidenceAttachmentUseCaseParamTest {

    @Test
    void testDeleteEvidenceAttachmentParam_NullEvidenceId_ErrorMessage() {
        UUID attachmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceAttachmentUseCase.Param(null, attachmentId, currentUserId));
        assertThat(throwable).hasMessage("evidenceId: " + DELETE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testDeleteEvidenceAttachmentParam_NullAttachmentId_ErrorMessage() {
        UUID evidenceId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceAttachmentUseCase.Param(evidenceId, null, currentUserId));
        assertThat(throwable).hasMessage("attachmentId: " + DELETE_EVIDENCE_ATTACHMENT_ATTACHMENT_ID_NOT_NULL);
    }

    @Test
    void testDeleteEvidenceAttachmentParam_NullCurrentUserId_ErrorMessage() {
        UUID evidenceId = UUID.randomUUID();
        UUID attachmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceAttachmentUseCase.Param(evidenceId, attachmentId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
