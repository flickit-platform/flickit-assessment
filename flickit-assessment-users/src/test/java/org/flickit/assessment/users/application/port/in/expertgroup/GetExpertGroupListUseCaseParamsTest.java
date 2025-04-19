package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetExpertGroupListUseCaseParamsTest {

    @Test
    void testGetExpertGroupListUseCaseParam_sizeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwable).hasMessage("size: " + GET_EXPERT_GROUP_LIST_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_EXPERT_GROUP_LIST_SIZE_MAX);
    }

    @Test
    void testGetExpertGroupListUseCaseParam_pageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_EXPERT_GROUP_LIST_PAGE_MIN);
    }

    @Test
    void testGetExpertGroupListUseCaseParam_currentUserIdParamIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetExpertGroupListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetExpertGroupListUseCase.Param.ParamBuilder paramBuilder() {
        return GetExpertGroupListUseCase.Param.builder()
            .size(10)
            .page(0)
            .currentUserId(UUID.randomUUID());
    }
}
