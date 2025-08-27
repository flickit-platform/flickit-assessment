package org.flickit.assessment.kit.application.port.in.kitbanner;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SLIDER_BANNERS_LANGUAGE_INVALID;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SLIDER_BANNERS_LANG_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetKitSliderBannersUseCaseParamTest {

    @Test
    void testGetKitSliderBannersUseCaseParam_langParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.lang(null)));
        assertThat(throwable).hasMessage("lang: " + GET_KIT_SLIDER_BANNERS_LANG_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
                () -> createParam(b -> b.lang("FR")));
        assertThat(throwable).hasMessage("lang: " + GET_KIT_SLIDER_BANNERS_LANGUAGE_INVALID);
    }

    private GetKitSliderBannersUseCase.Param createParam(Consumer<GetKitSliderBannersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetKitSliderBannersUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitSliderBannersUseCase.Param.builder()
                .lang("FA");
    }
}
