package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateAnswerRangeUseCaseParamTest {

    @Test
    void testUpdateAnswerRangeUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_answerRangeIdParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.answerRangeId(null)));
        assertThat(throwable).hasMessage("answerRangeId: " + UPDATE_ANSWER_RANGE_ANSWER_RANGE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ANSWER_RANGE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_ANSWER_RANGE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_reusableParamViolatesConstraints_ErrorMessage() {
        ConstraintViolationException throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.reusable(null)));
        assertThat(throwable).hasMessage("reusable: " + UPDATE_ANSWER_RANGE_REUSABLE_NOT_NULL);
    }

    @Test
    void testUpdateAnswerRangeUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private UpdateAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAnswerRangeUseCase.Param.builder()
                .kitVersionId(1L)
                .answerRangeId(2L)
                .title("title")
                .reusable(true)
                .currentUserId(UUID.randomUUID());
    }
}
