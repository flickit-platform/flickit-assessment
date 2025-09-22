package org.flickit.assessment.common.application.domain.kit.translation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.flickit.assessment.common.error.ErrorMessageKey.TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MAX;
import static org.flickit.assessment.common.error.ErrorMessageKey.TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MIN;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AnswerOptionTranslation(
    @Size(min = 3, message = TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MIN)
    @Size(max = 100, message = TRANSLATION_ANSWER_OPTION_TITLE_SIZE_MAX)
    String title
) {
    public String titleOrDefault(String defaultTitle) {
        return isBlank(title) ? defaultTitle : title;
    }
}
