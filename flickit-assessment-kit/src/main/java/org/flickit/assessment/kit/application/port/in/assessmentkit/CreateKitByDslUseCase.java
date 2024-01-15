package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateKitByDslUseCase {

    Long create(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_KIT_BY_DSL_KIT_DSL_JSON_ID_NOT_NULL)
        Long kitDslId;

        @NotNull(message = CREATE_KIT_BY_DSL_IS_PRIVATE_NOT_NULL)
        Boolean isPrivate;

        @NotNull(message = CREATE_KIT_BY_DSL_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotBlank(message = CREATE_KIT_BY_DSL_TITLE_NOT_NULL)
        String title;

        @NotBlank(message = CREATE_KIT_BY_DSL_SUMMARY_NOT_NULL)
        String summary;

        @NotBlank(message = CREATE_KIT_BY_DSL_ABOUT_NOT_NULL)
        String about;

        @NotNull(message = CREATE_KIT_BY_DSL_TAG_IDS_NOT_NULL)
        List<Long> tagIds;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitDslId, Boolean isPrivate, Long expertGroupId, String title, String summary, String about, List<Long> tagIds, UUID currentUserId) {
            this.kitDslId = kitDslId;
            this.isPrivate = isPrivate;
            this.expertGroupId = expertGroupId;
            this.title = title;
            this.summary = summary;
            this.about = about;
            this.tagIds = tagIds;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
