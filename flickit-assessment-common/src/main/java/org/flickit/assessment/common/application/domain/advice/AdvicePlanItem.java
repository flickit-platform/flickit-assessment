package org.flickit.assessment.common.application.domain.advice;

import java.util.List;

public record AdvicePlanItem(
    AdviceQuestion question,
    AdviceOption answeredOption,
    AdviceOption recommendedOption,
    List<AdviceAttribute> attributes,
    AdviceQuestionnaire questionnaire) {
}
