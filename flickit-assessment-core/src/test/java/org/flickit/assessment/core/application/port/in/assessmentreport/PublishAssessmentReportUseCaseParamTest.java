package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.PUBLISH_ASSESSMENT_REPORT_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublishAssessmentReportUseCaseParamTest {

    @Test
    void testPublishAssessmentReportUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + PUBLISH_ASSESSMENT_REPORT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testPublishAssessmentReportUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<PublishAssessmentReportUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private PublishAssessmentReportUseCase.Param.ParamBuilder paramBuilder() {
        return PublishAssessmentReportUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}