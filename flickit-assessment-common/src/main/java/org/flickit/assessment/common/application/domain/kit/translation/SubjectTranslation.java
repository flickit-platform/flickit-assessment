package org.flickit.assessment.common.application.domain.kit.translation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SubjectTranslation(
    @Size(min = 3, message = TRANSLATION_SUBJECT_TITLE_SIZE_MIN)
    @Size(max = 100, message = TRANSLATION_SUBJECT_TITLE_SIZE_MAX)
    String title,

    @Size(min = 3, message = TRANSLATION_SUBJECT_DESCRIPTION_SIZE_MIN)
    @Size(max = 500, message = TRANSLATION_SUBJECT_DESCRIPTION_SIZE_MAX)
    String description
) {
    public String titleOrDefault(String defaultTitle) {
        return isBlank(title) ? defaultTitle : title;
    }

    public String descriptionOrDefault(String defaultDescription) {
        return isBlank(description) ? defaultDescription : description;
    }
}
