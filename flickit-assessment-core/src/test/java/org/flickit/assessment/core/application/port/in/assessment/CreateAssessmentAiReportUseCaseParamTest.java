package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_LINK_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAssessmentAiReportUseCaseParamTest {

    @Test
    void testCreateAssessmentAttributeAiReport_AssessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(null, "pictureLink", currentUserId));
        assertThat(throwable).hasMessage("id: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_PictureLinkIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(id, null, currentUserId));
        assertThat(throwable).hasMessage("fileLink: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_LINK_NOT_NULL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_CurrentUserIsNull_ErrorMessage() {
        var id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(id, "pictureLink", null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
