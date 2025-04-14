package org.flickit.assessment.kit.application.port.in.kitbanner;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitSliderBannersUseCase {

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_SLIDER_BANNERS_LANG_ID_NOT_NULL)
        Integer langId;

        @Builder
        public Param(Integer langId) {
            this.langId = langId;
            this.validateSelf();
        }
    }
}
