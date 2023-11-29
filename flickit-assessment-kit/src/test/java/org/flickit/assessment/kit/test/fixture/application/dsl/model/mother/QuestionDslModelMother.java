package org.flickit.assessment.kit.test.fixture.application.dsl.model.mother;

import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;

import java.util.List;

public class QuestionDslModelMother {

    public static QuestionDslModel questionDslModel(
        String code,
        Integer index,
        String title,
        String description,
        String questionnaireCode,
        List<QuestionImpactDslModel> questionImpacts,
        List<AnswerOptionDslModel> answerOptions,
        boolean mayNotBeApplicable) {

        QuestionDslModel dslQuestion = new QuestionDslModel();
        dslQuestion.setCode(code);
        dslQuestion.setIndex(index);
        dslQuestion.setTitle(title);
        dslQuestion.setDescription(description);
        dslQuestion.setQuestionnaireCode(questionnaireCode);
        dslQuestion.setQuestionImpacts(questionImpacts);
        dslQuestion.setAnswerOptions(answerOptions);
        dslQuestion.setMayNotBeApplicable(mayNotBeApplicable);
        return dslQuestion;
    }
}
