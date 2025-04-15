package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_KIT_LANGUAGE_NOT_VALID;
import static org.flickit.assessment.common.validation.EnumValidateUtils.validateAndConvert;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateMaturityLevelUseCase {

    long createMaturityLevel(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_MATURITY_LEVEL_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = CREATE_MATURITY_LEVEL_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = CREATE_MATURITY_LEVEL_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_MATURITY_LEVEL_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_MATURITY_LEVEL_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_MATURITY_LEVEL_DESCRIPTION_NOT_NULL)
        @Size(min = 3, message = CREATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN)
        @Size(max = 500, message = CREATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = CREATE_MATURITY_LEVEL_VALUE_NOT_NULL)
        Integer value;

        @Valid
        @Nullable
        Map<KitLanguage, MaturityLevelTranslation> translations;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId,
                     Integer index,
                     String title,
                     String description,
                     Integer value,
                     Map<String, MaturityLevelTranslation> translations,
                     UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.index = index;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.description = description != null && !description.isBlank() ? description.trim() : null;
            this.value = value;
            this.translations = validateAndConvert(translations, KitLanguage.class, COMMON_KIT_LANGUAGE_NOT_VALID);
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
