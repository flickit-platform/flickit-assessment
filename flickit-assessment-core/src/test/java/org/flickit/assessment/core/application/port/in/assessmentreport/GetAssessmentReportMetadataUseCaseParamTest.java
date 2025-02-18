package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetAssessmentReportMetadataUseCaseParamTest {

    @Test
    void testGetAssessmentReportMetadataUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAssessmentReportMetadataUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAssessmentReportMetadataUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAssessmentReportMetadataUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentReportMetadataUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
