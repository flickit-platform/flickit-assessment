package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;

import java.util.function.Consumer;

public class AnswerOptionDslModelMother {

    public static AnswerOptionDslModel answerOptionDslModel(Integer index, String caption, Double value) {
        return AnswerOptionDslModel.builder()
            .index(index)
            .caption(caption)
            .value(value)
            .build();
    }

    public static AnswerOptionDslModel domainToDslModel(AnswerOption answerOption) {
        return domainToDslModel(answerOption, b -> {
        });
    }

    public static AnswerOptionDslModel domainToDslModel(AnswerOption answerOption,
                                                        Consumer<AnswerOptionDslModel.AnswerOptionDslModelBuilder> changer) {
        var builder = domainToDslModelBuilder(answerOption);
        changer.accept(builder);
        return builder.build();
    }

    private static AnswerOptionDslModel.AnswerOptionDslModelBuilder domainToDslModelBuilder(AnswerOption answerOption) {
        return AnswerOptionDslModel.builder()
            .index(answerOption.getIndex())
            .caption(answerOption.getTitle())
            .value(answerOption.getValue());
    }
}
