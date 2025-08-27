package org.flickit.assessment.common.application.domain.kit.translation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record QuestionTranslation(
    @Size(min = 3, message = TRANSLATION_QUESTION_TITLE_SIZE_MIN)
    @Size(max = 250, message = TRANSLATION_QUESTION_TITLE_SIZE_MAX)
    String title,

    @Size(min = 3, message = TRANSLATION_QUESTION_HINT_SIZE_MIN)
    @Size(max = 1000, message = TRANSLATION_QUESTION_HINT_SIZE_MAX)
    String hint
) {
    public String titleOrDefault(String defaultTitle) {
        return isBlank(title) ? defaultTitle : title;
    }

    public String hintOrDefault(String defaultHint) {
        return isBlank(hint) ? defaultHint : hint;
    }
}
