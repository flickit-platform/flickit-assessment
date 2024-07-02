package org.flickit.assessment.core.application.port.in.evidenceattachment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_ATTACHMENT_ATTACHMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class DeleteEvidenceAttachmentUseCaseParamTest {

    UUID evidenceId;
    UUID attachmentId;
    UUID currentUserId;

    @BeforeEach
    void setUp() {
        evidenceId = UUID.randomUUID();
        attachmentId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testDeleteEvidenceAttachmentParam_NullEvidenceId_ErrorMessage() {
        evidenceId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceAttachmentUseCase.Param(evidenceId, attachmentId, currentUserId));
        assertThat(throwable).hasMessage("evidenceId: " + DELETE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testDeleteEvidenceAttachmentParam_NullAttachmentId_ErrorMessage() {
        attachmentId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceAttachmentUseCase.Param(evidenceId, attachmentId, currentUserId));
        assertThat(throwable).hasMessage("attachmentId: " + DELETE_EVIDENCE_ATTACHMENT_ATTACHMENT_ID_NOT_NULL);
    }

    @Test
    void testDeleteEvidenceAttachmentParam_NullCurrentUserId_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new DeleteEvidenceAttachmentUseCase.Param(evidenceId, attachmentId, currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
