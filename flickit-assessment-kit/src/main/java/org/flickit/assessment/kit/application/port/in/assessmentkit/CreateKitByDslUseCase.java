package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateKitByDslUseCase {

    void create(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_KIT_BY_DSL_KIT_DSL_JSON_ID_NOT_NULL)
        Long kitJsonDslId;

        @NotBlank(message = CREATE_KIT_BY_DSL_TITLE_NOT_NULL)
        String title;

        @NotBlank(message = CREATE_KIT_BY_DSL_SUMMARY_NOT_NULL)
        String summary;

        @NotBlank(message = CREATE_KIT_BY_DSL_ABOUT_NOT_NULL)
        String about;

        @NotBlank(message = CREATE_KIT_BY_DSL_TAGS_NOT_NULL)
        String tags;

        public Param(Long kitJsonDslId, String title, String summary, String about, String tags) {
            this.kitJsonDslId = kitJsonDslId;
            this.title = title;
            this.summary = summary;
            this.about = about;
            this.tags = tags;
            this.validateSelf();
        }
    }
}
