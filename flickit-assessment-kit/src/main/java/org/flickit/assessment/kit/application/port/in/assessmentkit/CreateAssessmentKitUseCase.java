package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

public interface CreateAssessmentKitUseCase {

    Result createAssessmentKit(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotBlank(message = CREATE_ASSESSMENT_KIT_TITLE_NOT_NULL)
        @Size(min = 3, message = CREATE_ASSESSMENT_KIT_TITLE_SIZE_MIN)
        @Size(max = 100, message = CREATE_ASSESSMENT_KIT_TITLE_SIZE_MAX)
        String title;

        @NotBlank(message = CREATE_ASSESSMENT_KIT_SUMMARY_NOT_NULL)
        @Size(min = 3, message = CREATE_ASSESSMENT_KIT_SUMMARY_SIZE_MIN)
        @Size(max = 1000, message = CREATE_ASSESSMENT_KIT_SUMMARY_SIZE_MAX)
        String summary;

        @NotBlank(message = CREATE_ASSESSMENT_KIT_ABOUT_NOT_NULL)
        @Size(min = 3, message = CREATE_ASSESSMENT_KIT_ABOUT_SIZE_MIN)
        @Size(max = 1000, message = CREATE_ASSESSMENT_KIT_ABOUT_SIZE_MAX)
        String about;

        @EnumValue(enumClass = KitLanguage.class, message = CREATE_ASSESSMENT_KIT_LANGUAGE_INVALID)
        String lang;

        @NotNull(message = CREATE_ASSESSMENT_KIT_IS_PRIVATE_NOT_NULL)
        Boolean isPrivate;

        @NotNull(message = CREATE_ASSESSMENT_KIT_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotEmpty(message = CREATE_ASSESSMENT_KIT_TAG_IDS_NOT_NULL)
        List<Long> tagIds;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(String title,
                     String summary,
                     String about,
                     String lang,
                     Boolean isPrivate,
                     Long expertGroupId,
                     List<Long> tagIds,
                     UUID currentUserId) {
            this.title = title != null ? title.strip() : null;
            this.summary = summary != null ? summary.strip() : null;
            this.about = about != null ? about.strip() : null;
            this.lang = KitLanguage.getEnum(lang).name();
            this.isPrivate = isPrivate;
            this.expertGroupId = expertGroupId;
            this.tagIds = tagIds;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(long kitId) {
    }
}
