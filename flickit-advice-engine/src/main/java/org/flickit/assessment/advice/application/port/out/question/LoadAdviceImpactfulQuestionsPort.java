package org.flickit.assessment.advice.application.port.out.question;

import org.flickit.assessment.advice.application.domain.advice.AttributeListItem;
import org.flickit.assessment.advice.application.domain.advice.OptionListItem;
import org.flickit.assessment.advice.application.domain.advice.QuestionnaireListItem;

import java.util.List;

public interface LoadAdviceImpactfulQuestionsPort {

    List<Result> loadQuestions(List<Long> questionIds);

    record Result(
        long id,
        String title,
        int index,
        List<OptionListItem> options,
        List<AttributeListItem> attributes,
        QuestionnaireListItem questionnaire
    ) {
    }
}
