package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateKitByDslUseCase {

    Long create(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_KIT_BY_DSL_KIT_DSL_JSON_ID_NOT_NULL)
        Long kitJsonDslId;

        @NotNull(message = CREATE_KIT_BY_DSL_IS_PRIVATE_NOT_NULL)
        boolean isPrivate;

        @NotNull(message = CREATE_KIT_BY_DSL_EXPERT_GROUP_ID_NOT_NULL)
        Long expertGroupId;

        @NotBlank(message = CREATE_KIT_BY_DSL_TITLE_NOT_NULL)
        String title;

        @NotBlank(message = CREATE_KIT_BY_DSL_SUMMARY_NOT_NULL)
        String summary;

        @NotBlank(message = CREATE_KIT_BY_DSL_ABOUT_NOT_NULL)
        String about;

        @NotBlank(message = CREATE_KIT_BY_DSL_TAG_IDS_NOT_NULL)
        String[] tagIds;

        public Param(Long kitJsonDslId, boolean isPrivate, Long expertGroupId, String title, String summary, String about, String[] tagIds) {
            this.kitJsonDslId = kitJsonDslId;
            this.isPrivate = isPrivate;
            this.expertGroupId = expertGroupId;
            this.title = title;
            this.summary = summary;
            this.about = about;
            this.tagIds = tagIds;
            this.validateSelf();
        }
    }
}
