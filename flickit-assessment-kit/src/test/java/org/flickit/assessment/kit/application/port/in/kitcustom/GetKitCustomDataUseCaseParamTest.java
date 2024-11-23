package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class GetKitCustomDataUseCaseParamTest {

    @Test
    void testGetKitCustomDataUseCaseParam_kitIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_CUSTOM_DATA_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitCustomDataUseCaseParam_pageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_KIT_CUSTOM_DATA_PAGE_MIN);
    }

    @Test
    void testGetKitCustomDataUseCaseParam_sizeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.size(-1)));
        assertThat(throwable).hasMessage("size: " + GET_KIT_CUSTOM_DATA_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_KIT_CUSTOM_DATA_SIZE_MAX);
    }

    @Test
    void testGetKitCustomDataUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.currentUserId(null)));
        AssertionsForClassTypes.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetKitCustomDataUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private GetKitCustomDataUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitCustomDataUseCase.Param.builder()
            .kitId(1L)
            .kitCustomId(2L)
            .currentUserId(UUID.randomUUID())
            .page(1)
            .size(2);
    }
}
