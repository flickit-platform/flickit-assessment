package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetAttributeScoreDetailUseCaseTest {

    @Test
    void testGetAttributeScoreDetail_AssessmentIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeScoreDetailUseCase.Param(null, 1L, 1L, userId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetail_AttributeIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeScoreDetailUseCase.Param(assessmentId, null, 1L, userId));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_SCORE_DETAIL_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetail_MaturityLevelIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeScoreDetailUseCase.Param(assessmentId, 1L, null, userId));
        assertThat(throwable).hasMessage("maturityLevelId: " + GET_ATTRIBUTE_SCORE_DETAIL_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void testGetAttributeScoreDetail_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetAttributeScoreDetailUseCase.Param(assessmentId, 1L, 1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
