package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_PUBLISH_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAssessmentReportPublishStatusUseCaseParamTest {

    @Test
    void testUpdateAssessmentReportPublishStatusUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentReportPublishStatusUseCaseParam_publishedParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.published(null)));
        assertThat(throwable).hasMessage("published: " + UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_PUBLISH_NOT_NULL);
    }

    @Test
    void testUpdateAssessmentReportPublishStatusUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateAssessmentReportPublishStatusUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAssessmentReportPublishStatusUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentReportPublishStatusUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .published(Boolean.TRUE)
            .currentUserId(UUID.randomUUID());
    }
}
