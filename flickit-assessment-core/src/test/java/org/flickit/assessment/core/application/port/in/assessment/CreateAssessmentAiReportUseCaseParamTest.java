package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAssessmentAiReportUseCaseParamTest {

    @Test
    void testCreateAssessmentAttributeAiReport_AssessmentIdIsNull_ErrorMessage() {
        var attributeId = 1L;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(null, attributeId, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AttributeIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_CurrentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(assessmentId, attributeId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
