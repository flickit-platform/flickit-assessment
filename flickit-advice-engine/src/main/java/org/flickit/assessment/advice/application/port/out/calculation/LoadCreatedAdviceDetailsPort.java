package org.flickit.assessment.advice.application.port.out.calculation;

import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;

import java.util.List;

public interface LoadCreatedAdviceDetailsPort {

    List<Result> loadAdviceDetails(List<Long> questionIds, Long kitVersionId);

    record Result(
        AdviceQuestion question,
        List<AdviceOption> options,
        List<AdviceAttribute> attributes,
        AdviceQuestionnaire questionnaire
    ) {
    }
}
