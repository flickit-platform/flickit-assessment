package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_RANGE_TITLE_SIZE_MAX;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_RANGE_TITLE_SIZE_MIN;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAnswerRangeUseCaseParamTest {

    @Test
    void testCreateAnswerRangeUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + CREATE_ANSWER_RANGE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_ANSWER_RANGE_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateAnswerRangeUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private CreateAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAnswerRangeUseCase.Param.builder()
            .title("title")
            .currentUserId(UUID.randomUUID());
    }
}
