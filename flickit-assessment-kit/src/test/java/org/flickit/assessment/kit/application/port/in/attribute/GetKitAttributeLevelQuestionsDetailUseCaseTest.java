package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitAttributeLevelQuestionsDetailUseCaseTest {

    @Test
    void getAttrLevelQuestionsInfoUseCase_KitIdIsNull_ShouldThrowException() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitAttributeLevelQuestionsDetailUseCase.Param(null, 1L, 1L, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + GET_ATTRIBUTE_LEVEL_QUESTIONS_KIT_ID_NOT_NULL);
    }

    @Test
    void getAttrLevelQuestionsInfoUseCase_AttributeIdIsNull_ShouldThrowException() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitAttributeLevelQuestionsDetailUseCase.Param(1L, null, 1L, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + GET_ATTRIBUTE_LEVEL_QUESTIONS_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void getAttrLevelQuestionsInfoUseCase_MaturityLevelIdIsNull_ShouldThrowException() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitAttributeLevelQuestionsDetailUseCase.Param(1L, 1L, null, currentUserId));
        assertThat(throwable).hasMessage("maturityLevelId: " + GET_ATTRIBUTE_LEVEL_QUESTIONS_MATURITY_LEVEL_ID_NOT_NULL);
    }

    @Test
    void getAttrLevelQuestionsInfoUseCase_CurrentUserIdIsNull_ShouldThrowException() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitAttributeLevelQuestionsDetailUseCase.Param(1L, 1L, 1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
