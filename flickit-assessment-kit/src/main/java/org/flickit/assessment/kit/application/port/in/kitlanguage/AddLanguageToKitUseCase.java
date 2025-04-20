package org.flickit.assessment.kit.application.port.in.kitlanguage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface AddLanguageToKitUseCase {

    void addLanguageToKit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ADD_LANGUAGE_TO_KIT_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = ADD_LANGUAGE_TO_KIT_LANG_NOT_NULL)
        @EnumValue(enumClass = KitLanguage.class, message = ADD_LANGUAGE_TO_KIT_LANGUAGE_INVALID)
        String lang;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId, String lang, UUID currentUserId) {
            this.kitId = kitId;
            this.lang = lang;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
