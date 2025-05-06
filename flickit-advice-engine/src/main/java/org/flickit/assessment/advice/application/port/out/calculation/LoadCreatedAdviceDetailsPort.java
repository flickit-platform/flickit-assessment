package org.flickit.assessment.advice.application.port.out.calculation;

import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;

import java.util.List;
import java.util.UUID;

public interface LoadCreatedAdviceDetailsPort {

    List<Result> loadAdviceDetails(List<Long> questionIds, UUID assessmentId);

    record Result(
        AdviceQuestion question,
        List<AdviceOption> options,
        List<AdviceAttribute> attributes,
        AdviceQuestionnaire questionnaire
    ) {
    }
}
