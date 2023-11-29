package org.flickit.assessment.kit.test.fixture.application.dsl;

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

        return QuestionDslModel.builder()
            .code(code)
            .index(index)
            .title(title)
            .description(description)
            .questionnaireCode(questionnaireCode)
            .questionImpacts(questionImpacts)
            .answerOptions(answerOptions)
            .mayNotBeApplicable(mayNotBeApplicable)
            .build();
    }
}
