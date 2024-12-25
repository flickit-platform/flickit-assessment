package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class ApproveAttributeInsightUseCaseParamTest {

    @Test
    void testApproveAttributeInsightUseCaseParam_assessmentIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + APPROVE_ATTRIBUTE_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testApproveAttributeInsightUseCaseParam_attributeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeId(null)));
        assertThat(throwable).hasMessage("attributeId: " + APPROVE_ATTRIBUTE_INSIGHT_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testApproveAttributeInsightUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<ApproveAttributeInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private ApproveAttributeInsightUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveAttributeInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
