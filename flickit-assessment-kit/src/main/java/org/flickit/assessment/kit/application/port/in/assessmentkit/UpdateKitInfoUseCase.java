package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.jsonwebtoken.lang.Collections.isEmpty;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.common.validation.EnumValidateUtils.validateAndConvert;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateKitInfoUseCase {

    void updateKitInfo(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_KIT_INFO_KIT_ID_NOT_NULL)
        Long kitId;

        @Size(min = 3, message = UPDATE_KIT_INFO_TITLE_SIZE_MIN)
        @Size(max = 50, message = UPDATE_KIT_INFO_TITLE_SIZE_MAX)
        String title;

        @Size(min = 3, message = UPDATE_KIT_INFO_SUMMARY_SIZE_MIN)
        @Size(max = 200, message = UPDATE_KIT_INFO_SUMMARY_SIZE_MAX)
        String summary;

        @EnumValue(enumClass = KitLanguage.class, message = UPDATE_KIT_INFO_KIT_LANGUAGE_INVALID)
        String lang;

        Boolean published;

        Boolean isPrivate;

        Double price;

        @Size(min = 3, message = UPDATE_KIT_INFO_ABOUT_SIZE_MIN)
        @Size(max = 1000, message = UPDATE_KIT_INFO_ABOUT_SIZE_MAX)
        String about;

        @Size(min = 1, message = UPDATE_KIT_INFO_TAGS_SIZE_MIN)
        List<Long> tags;

        @Valid
        Map<KitLanguage, KitTranslation> translations;

        boolean removeTranslations;

        MetadataParam metadata;

        boolean removeMetadata;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @AssertTrue(message = UPDATE_KIT_INFO_TRANSLATIONS_INCORRECT)
        boolean isTranslationFieldCorrect() {
            return !removeTranslations || isEmpty(translations);
        }

        @AssertTrue(message = UPDATE_KIT_INFO_METADATA_INCORRECT)
        boolean isMetadataFieldCorrect() {
            return !removeMetadata || (metadata.goal == null && metadata.context == null);
        }

        @Builder
        public Param(Long kitId,
                     String title,
                     String summary,
                     String lang,
                     Boolean published,
                     Boolean isPrivate,
                     Double price,
                     String about,
                     List<Long> tags,
                     Map<String, KitTranslation> translations,
                     boolean removeTranslations,
                     MetadataParam metadata,
                     boolean removeMetadata,
                     UUID currentUserId) {
            this.kitId = kitId;
            this.title = title;
            this.summary = summary;
            this.lang = lang != null ? KitLanguage.getEnum(lang).name() : null;
            this.published = published;
            this.isPrivate = isPrivate;
            this.price = price;
            this.about = about;
            this.tags = tags;
            this.translations = validateAndConvert(translations, KitLanguage.class, COMMON_KIT_LANGUAGE_NOT_VALID);
            this.removeTranslations = removeTranslations;
            this.metadata = metadata;
            this.removeMetadata = removeMetadata;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    class MetadataParam extends SelfValidating<MetadataParam> {

        @Size(min = 3, message = UPDATE_KIT_INFO_METADATA_GOAL_SIZE_MIN)
        @Size(max = 300, message = UPDATE_KIT_INFO_METADATA_GOAL_SIZE_MAX)
        String goal;

        @Size(min = 3, message = UPDATE_KIT_INFO_METADATA_CONTEXT_SIZE_MIN)
        @Size(max = 300, message = UPDATE_KIT_INFO_METADATA_CONTEXT_SIZE_MAX)
        String context;

        @Builder
        public MetadataParam(String goal, String context) {
            this.goal = goal != null && !goal.isBlank() ? goal.strip() : null;
            this.context = context != null && !context.isBlank() ? context.strip() : null;
            this.validateSelf();
        }
    }
}
