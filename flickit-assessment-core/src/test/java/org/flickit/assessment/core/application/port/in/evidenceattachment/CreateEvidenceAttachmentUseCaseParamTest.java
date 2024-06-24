package org.flickit.assessment.core.application.port.in.evidenceattachment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_EVIDENCE_ATTACHMENT_ATTACHMENT_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateEvidenceAttachmentUseCaseParamTest {

    @Test
    void testCreateEvidenceAttachmentParam_evidenceIdIsNull_ErrorMessage() throws IOException {
        UUID currentUserId = UUID.randomUUID();
        MockMultipartFile attachment = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateEvidenceAttachmentUseCase.Param(null, attachment, currentUserId));
        assertThat(throwable).hasMessage("evidenceId: " + CREATE_EVIDENCE_ATTACHMENT_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testCreateEvidenceAttachmentParam_attachmentIsNull_ErrorMessage() {
        UUID evidenceId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateEvidenceAttachmentUseCase.Param(evidenceId, null, currentUserId));
        assertThat(throwable).hasMessage("attachment: " + CREATE_EVIDENCE_ATTACHMENT_ATTACHMENT_NOT_NULL);
    }

    @Test
    void testCreateEvidenceAttachmentParam_currentUserIdIsNull_ErrorMessage() throws IOException {
        UUID evidenceId = UUID.randomUUID();
        MockMultipartFile attachment = new MockMultipartFile("images", "image1",
            "image/png", new ByteArrayInputStream("Some content".getBytes()));
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateEvidenceAttachmentUseCase.Param(evidenceId, attachment, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
