package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.validation.EnumValue;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateKitByDslUseCase {

    Long create(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotBlank(message = CREATE_KIT_BY_DSL_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_KIT_BY_DSL_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_KIT_BY_DSL_TITLE_SIZE_MAX)
        String title;

        @NotBlank(message = CREATE_KIT_BY_DSL_SUMMARY_NOT_NULL)
        @Size(min = 3, message = CREATE_KIT_BY_DSL_SUMMARY_SIZE_MIN)
        @Size(max = 1000, message = CREATE_KIT_BY_DSL_SUMMARY_SIZE_MAX)
        String summary;

        @NotBlank(message = CREATE_KIT_BY_DSL_ABOUT_NOT_NULL)
        @Size(min = 3, message = CREATE_KIT_BY_DSL_ABOUT_SIZE_MIN)
        @Size(max = 1000, message = CREATE_KIT_BY_DSL_ABOUT_SIZE_MAX)
        String about;

        @EnumValue(enumClass = KitLanguage.class, message = CREATE_KIT_BY_DSL_LANGUAGE_INVALID)
        String lang;

        @NotNull(message = CREATE_KIT_BY_DSL_IS_PRIVATE_NOT_NULL)
        Boolean isPrivate;

        @NotNull(message = CREATE_KIT_BY_DSL_KIT_DSL_ID_NOT_NULL)
        Long kitDslId;

        @NotNull(message = CREATE_KIT_BY_DSL_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotNull(message = CREATE_KIT_BY_DSL_TAG_IDS_NOT_NULL)
        List<Long> tagIds;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(String title,
                     String summary,
                     String about,
                     String lang,
                     Boolean isPrivate,
                     Long kitDslId,
                     Long expertGroupId,
                     List<Long> tagIds,
                     UUID currentUserId) {
            this.title = title != null ? title.strip() : null;
            this.summary = summary != null ? summary.strip() : null;
            this.about = about != null ? about.strip() : null;
            this.lang = KitLanguage.getEnum(lang).name();
            this.isPrivate = isPrivate;
            this.kitDslId = kitDslId;
            this.expertGroupId = expertGroupId;
            this.tagIds = tagIds;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
