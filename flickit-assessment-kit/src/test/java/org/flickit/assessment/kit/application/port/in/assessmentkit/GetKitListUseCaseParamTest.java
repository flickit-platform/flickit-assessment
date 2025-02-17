package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitListUseCaseParamTest {

    @Test
    void testGetAssessmentKitListUseCaseParam_IsPrivateParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.isPrivate(null)));
        assertThat(throwable).hasMessage("isPrivate: " + GET_KIT_LIST_IS_PRIVATE_NOT_NULL);
    }

    @Test
    void testGetAssessmentKitListUseCaseParam_LanguageParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.language("invalid")));
        assertThat(throwable).hasMessage("language: " + GET_KIT_LIST_LANGUAGE_INVALID);
    }

    @Test
    void testGetAssessmentKitListUseCaseParam_PageParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_KIT_LIST_PAGE_MIN);
    }

    @Test
    void testGetAssessmentKitListUseCaseParam_SizeParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(-1)));
        assertThat(throwable).hasMessage("size: " + GET_KIT_LIST_SIZE_MIN);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_KIT_LIST_SIZE_MAX);
    }

    @Test
    void testGetAssessmentKitListUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .isPrivate(true)
            .language(KitLanguage.EN.name())
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
