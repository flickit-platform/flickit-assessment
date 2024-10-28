package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_ANSWER_RANGE_LIST_KIT_VERSION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetAnswerRangeListUseCaseTest {

    @Test
    void testGetAnswerRangeListParam_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_ANSWER_RANGE_LIST_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testGetAnswerRangeListParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetAnswerRangeListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetAnswerRangeListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAnswerRangeListUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
