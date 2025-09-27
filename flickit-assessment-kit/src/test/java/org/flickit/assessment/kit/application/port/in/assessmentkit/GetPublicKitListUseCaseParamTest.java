package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublicKitListUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetPublicKitListUseCaseParamTest {

    @Test
    void testGetPublicKitListUseCaseParam_LangsParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.langs(Set.of("invalid"))));
        assertThat(throwable).hasMessage("langs: " + GET_PUBLIC_KIT_LIST_LANGS_INVALID);
    }

    @Test
    void testGetPublicKitListUseCaseParam_PageParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_PUBLIC_KIT_LIST_PAGE_MIN);
    }

    @Test
    void testGetPublicKitListUseCaseParam_SizeParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(-1)));
        assertThat(throwable).hasMessage("size: " + GET_PUBLIC_KIT_LIST_SIZE_MIN);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_PUBLIC_KIT_LIST_SIZE_MAX);
    }

    private void createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .langs(Set.of(KitLanguage.EN.name()))
            .page(0)
            .size(10);
    }
}
