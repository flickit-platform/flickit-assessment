package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;

import java.util.function.Consumer;

public class AnswerRangeDslModelMother {

    public static AnswerRangeDslModel domainToDslModel(AnswerRange answerRange) {
        return domainToDslModel(answerRange, b -> {
        });
    }

    public static AnswerRangeDslModel domainToDslModel(AnswerRange answerRange,
                                                       Consumer<AnswerRangeDslModel.AnswerRangeDslModelBuilder<?, ?>> changer) {
        var builder = domainToDslModelBuilder(answerRange);
        changer.accept(builder);
        return builder
            .answerOptions(null)
            .build();
    }

    private static AnswerRangeDslModel.AnswerRangeDslModelBuilder<?, ?> domainToDslModelBuilder(AnswerRange answerRange) {
        return AnswerRangeDslModel.builder()
            .code(answerRange.getCode())
            .title(answerRange.getTitle())
            .index(null)
            .description(null);
    }
}
