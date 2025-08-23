package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_KIT_LANGUAGE_NOT_VALID;
import static org.flickit.assessment.common.validation.EnumValidateUtils.validateAndConvert;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateAttributeUseCase {

    void updateAttribute(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = UPDATE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_ATTRIBUTE_INDEX_NOT_NULL)
        Integer index;

        @NotBlank(message = UPDATE_ATTRIBUTE_TITLE_NOT_BLANK)
        @Size(min = 3, message = UPDATE_ATTRIBUTE_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_ATTRIBUTE_TITLE_SIZE_MAX)
        String title;

        @NotBlank(message = UPDATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK)
        @Size(min = 3, message = UPDATE_ATTRIBUTE_DESCRIPTION_SIZE_MIN)
        @Size(max = 500, message = UPDATE_ATTRIBUTE_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = UPDATE_ATTRIBUTE_WEIGHT_NOT_NULL)
        Integer weight;

        @NotNull(message = UPDATE_ATTRIBUTE_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @Valid
        @Nullable
        Map<KitLanguage, AttributeTranslation> translations;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long attributeId,
                     Long kitVersionId,
                     Integer index,
                     String title,
                     String description,
                     Integer weight,
                     Long subjectId,
                     Map<String, AttributeTranslation> translations,
                     UUID currentUserId) {
            this.attributeId = attributeId;
            this.kitVersionId = kitVersionId;
            this.index = index;
            this.title = title;
            this.description = description;
            this.weight = weight;
            this.subjectId = subjectId;
            this.translations = validateAndConvert(translations, KitLanguage.class, COMMON_KIT_LANGUAGE_NOT_VALID);
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
