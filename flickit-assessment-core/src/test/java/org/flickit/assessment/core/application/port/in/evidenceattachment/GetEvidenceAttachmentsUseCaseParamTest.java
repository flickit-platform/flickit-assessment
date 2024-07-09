package org.flickit.assessment.core.application.port.in.evidenceattachment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ATTACHMENTS_EVIDENCE_ID_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetEvidenceAttachmentsUseCaseParamTest {

    @Test
    void testGetEvidenceAttachmentListParam_evidenceIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceAttachmentsUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("evidenceId: " + GET_EVIDENCE_ATTACHMENTS_EVIDENCE_ID_NULL);
    }

    @Test
    void testGetEvidenceAttachmentListParam_currentUserIdIsNull_ErrorMessage() {
        var evidenceId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceAttachmentsUseCase.Param(evidenceId,null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
