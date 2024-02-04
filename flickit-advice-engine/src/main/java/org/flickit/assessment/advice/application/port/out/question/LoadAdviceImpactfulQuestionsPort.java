package org.flickit.assessment.advice.application.port.out.question;

import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;

import java.util.List;

public interface LoadAdviceImpactfulQuestionsPort {

    List<Result> loadQuestions(List<Long> questionIds);

    record Result(
        long id,
        String title,
        int index,
        List<AdviceOption> options,
        List<AdviceAttribute> attributes,
        AdviceQuestionnaire questionnaire
    ) {
    }
}
