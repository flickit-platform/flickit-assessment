package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitCustomUseCaseParamTest {

    @Test
    void testGetKitCustomUseCaseParam_kitCustomIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.kitCustomId(null)));
        assertThat(throwable).hasMessage("kitCustomId: " + GET_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL);
    }

    @Test
    void testGetKitCustomUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.currentUserId(null)));
        AssertionsForClassTypes.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private GetKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitCustomUseCase.Param.builder()
            .kitCustomId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
