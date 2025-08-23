package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;

import java.util.List;
import java.util.function.Consumer;

public class QuestionDslModelMother {

    public static QuestionDslModel questionDslModel(
        String code,
        Integer index,
        String title,
        String description,
        String questionnaireCode,
        List<QuestionImpactDslModel> questionImpacts,
        List<AnswerOptionDslModel> answerOptions,
        String answerRangeCode,
        boolean mayNotBeApplicable,
        boolean advisable) {

        return QuestionDslModel.builder()
            .code(code)
            .index(index)
            .title(title)
            .description(description)
            .questionnaireCode(questionnaireCode)
            .questionImpacts(questionImpacts)
            .answerOptions(answerOptions)
            .answerRangeCode(answerRangeCode)
            .mayNotBeApplicable(mayNotBeApplicable)
            .advisable(advisable)
            .build();
    }

    public static QuestionDslModel domainToDslModel(Question question) {
        return domainToDslModel(question, b -> {
        });
    }

    public static QuestionDslModel domainToDslModel(Question question,
                                                    Consumer<QuestionDslModel.QuestionDslModelBuilder<?, ?>> changer) {
        var builder = domainToDslModelBuilder(question);
        changer.accept(builder);
        return builder.build();
    }

    private static QuestionDslModel.QuestionDslModelBuilder<?, ?> domainToDslModelBuilder(Question question) {
        return QuestionDslModel.builder()
            .code(question.getCode())
            .title(question.getTitle())
            .index(question.getIndex())
            .description(question.getHint());
    }
}
