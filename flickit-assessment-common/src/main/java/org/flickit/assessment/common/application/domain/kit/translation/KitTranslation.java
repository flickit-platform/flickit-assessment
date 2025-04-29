package org.flickit.assessment.common.application.domain.kit.translation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import static org.apache.commons.lang3.StringUtils.isBlank;
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
    String about,

    @Valid
    MetadataTranslation metadata
) {

    public String titleOrDefault(String defaultTitle) {
        return isBlank(title) ? defaultTitle : title;
    }

    public String summaryOrDefault(String defaultSummary) {
        return isBlank(summary) ? defaultSummary : summary;
    }

    public String aboutOrDefault(String defaultAbout) {
        return isBlank(about) ? defaultAbout : about;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record MetadataTranslation(

        @Size(min = 3, message = TRANSLATION_ASSESSMENT_KIT_METADATA_GOAL_SIZE_MIN)
        @Size(max = 300, message = TRANSLATION_ASSESSMENT_KIT_METADATA_GOAL_SIZE_MAX)
        String goal,

        @Size(min = 3, message = TRANSLATION_ASSESSMENT_KIT_METADATA_CONTEXT_SIZE_MIN)
        @Size(max = 300, message = TRANSLATION_ASSESSMENT_KIT_METADATA_CONTEXT_SIZE_MAX)
        String context
    ) {
    }
}
