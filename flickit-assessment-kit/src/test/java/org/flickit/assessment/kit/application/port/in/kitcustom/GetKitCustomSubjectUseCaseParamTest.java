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

class GetKitCustomSubjectUseCaseParamTest {

    @Test
    void testGetKitCustomSubjectUseCaseParam_kitIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_CUSTOM_SUBJECT_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitCustomSubjectUseCaseParam_pageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_KIT_CUSTOM_SUBJECT_PAGE_MIN);
    }

    @Test
    void testGetKitCustomSubjectUseCaseParam_sizeParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.size(-1)));
        assertThat(throwable).hasMessage("size: " + GET_KIT_CUSTOM_SUBJECT_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_KIT_CUSTOM_SUBJECT_SIZE_MAX);
    }

    @Test
    void testGetKitCustomSubjectUseCaseParam_currentUserParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> createParam(b -> b.currentUserId(null)));
        AssertionsForClassTypes.assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<GetKitCustomSubjectUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private GetKitCustomSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitCustomSubjectUseCase.Param.builder()
            .kitId(1L)
            .kitCustomId(2L)
            .currentUserId(UUID.randomUUID())
            .page(1)
            .size(2);
    }
}
