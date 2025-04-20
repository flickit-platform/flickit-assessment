package org.flickit.assessment.common.application.domain.kit.translation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AnswerRangeTranslation(
    @Size(min = 3, message = TRANSLATION_ANSWER_RANGE_TITLE_SIZE_MIN)
    @Size(max = 100, message = TRANSLATION_ANSWER_RANGE_TITLE_SIZE_MAX)
    String title
) {
}
