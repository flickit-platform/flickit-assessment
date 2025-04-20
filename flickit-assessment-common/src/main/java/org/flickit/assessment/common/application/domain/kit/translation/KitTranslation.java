package org.flickit.assessment.common.application.domain.kit.translation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record KitTranslation(
    @Size(min = 3, message = TRANSLATION_ASSESSMENT_KIT_TITLE_SIZE_MIN)
    @Size(max = 50, message = TRANSLATION_ASSESSMENT_KIT_TITLE_SIZE_MAX)
    String title,

    @Size(min = 3, message = TRANSLATION_ASSESSMENT_KIT_SUMMARY_SIZE_MIN)
    @Size(max = 200, message = TRANSLATION_ASSESSMENT_KIT_SUMMARY_SIZE_MAX)
    String summary,

    @Size(min = 3, message = TRANSLATION_ASSESSMENT_KIT_ABOUT_SIZE_MIN)
    @Size(max = 1000, message = TRANSLATION_ASSESSMENT_KIT_ABOUT_SIZE_MAX)
    String about
) {
}
