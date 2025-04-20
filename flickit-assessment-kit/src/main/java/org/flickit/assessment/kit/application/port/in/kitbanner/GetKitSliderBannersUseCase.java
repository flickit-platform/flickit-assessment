package org.flickit.assessment.kit.application.port.in.kitbanner;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SLIDER_BANNERS_LANGUAGE_INVALID;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SLIDER_BANNERS_LANG_NOT_NULL;

public interface GetKitSliderBannersUseCase {

    List<Result> getSliderBanners(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_SLIDER_BANNERS_LANG_NOT_NULL)
        @EnumValue(enumClass = KitLanguage.class, message = GET_KIT_SLIDER_BANNERS_LANGUAGE_INVALID)
        String lang;

        @Builder
        public Param(String lang) {
            this.lang = lang;
            this.validateSelf();
        }
    }

    record Result(long kitId, String banner) {
    }
}
