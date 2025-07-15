package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSpaceUseCaseParamsTest {

    @Test
    void testUpdateSpaceParam_idParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + UPDATE_SPACE_SPACE_ID_NOT_NULL);
    }

    @Test
    void testUpdateSpaceUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_SPACE_TITLE_NOT_BLANK);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.random(2, true, true))));
        assertThat(throwable).hasMessage("title: " + UPDATE_SPACE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.random(101, true, true))));
        assertThat(throwable).hasMessage("title: " + UPDATE_SPACE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateSpaceUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateSpaceUseCase.Param.builder()
            .id(0L)
            .title("title")
            .currentUserId(UUID.randomUUID());
    }
}
