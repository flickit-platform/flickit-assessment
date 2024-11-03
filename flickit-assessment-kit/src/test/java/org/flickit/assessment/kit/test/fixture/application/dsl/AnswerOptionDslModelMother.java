package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;

public class AnswerOptionDslModelMother {

    public static AnswerOptionDslModel answerOptionDslModel(Integer index, String caption, Double value) {
        return AnswerOptionDslModel.builder()
            .index(index)
            .caption(caption)
            .value(value)
            .build();
    }
}
