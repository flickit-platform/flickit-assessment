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
        var fileLink = "https://www.flickit.com/file/example.xlsx";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(null, attributeId, fileLink, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AttributeIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var fileLink = "https://www.flickit.com/file/example.xlsx";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(assessmentId, null, fileLink, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_PictureLinkIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(assessmentId, attributeId, null, currentUserId));
        assertThat(throwable).hasMessage("fileLink: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_LINK_NOT_NULL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_PictureLinkIsNotUrl_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var currentUserId = UUID.randomUUID();
        var fileLink = "invalidLink";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(assessmentId, attributeId, fileLink, currentUserId));
        assertThat(throwable).hasMessage("fileLink: " + CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_FILE_LINK_NOT_URL);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_CurrentUserIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var fileLink = "https://www.flickit.com/file/example.xlsx";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAttributeAiReportUseCase.Param(assessmentId, attributeId, fileLink, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
