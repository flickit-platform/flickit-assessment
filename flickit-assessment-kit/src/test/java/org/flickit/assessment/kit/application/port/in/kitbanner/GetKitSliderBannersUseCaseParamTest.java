package org.flickit.assessment.kit.application.port.in.kitbanner;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SLIDER_BANNERS_LANG_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class GetKitSliderBannersUseCaseParamTest {

    @Test
    void testGetKitSliderBannersUseCaseParam_kitIdParamViolate_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.langId(null)));
        assertThat(throwable).hasMessage("langId: " + GET_KIT_SLIDER_BANNERS_LANG_ID_NOT_NULL);
    }

    private GetKitSliderBannersUseCase.Param createParam(Consumer<GetKitSliderBannersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetKitSliderBannersUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitSliderBannersUseCase.Param.builder()
            .langId(1);
    }
}
