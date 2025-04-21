package org.flickit.assessment.common.application.domain.kit.translation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttributeTranslation(
    @Size(min = 3, message = TRANSLATION_ATTRIBUTE_TITLE_SIZE_MIN)
    @Size(max = 100, message = TRANSLATION_ATTRIBUTE_TITLE_SIZE_MAX)
    String title,

    @Size(min = 3, message = TRANSLATION_ATTRIBUTE_DESCRIPTION_SIZE_MIN)
    @Size(max = 500, message = TRANSLATION_ATTRIBUTE_DESCRIPTION_SIZE_MAX)
    String description
) {
    public String titleOrDefault(String defaultTitle) {
        return StringUtils.isBlank(title) ? defaultTitle : title;
    }

    public String descriptionOrDefault(String defaultDescription) {
        return StringUtils.isBlank(description) ? defaultDescription : description;
    }
}
