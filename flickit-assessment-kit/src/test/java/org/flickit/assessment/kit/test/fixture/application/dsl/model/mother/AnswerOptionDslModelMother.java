package org.flickit.assessment.kit.test.fixture.application.dsl.model.mother;

import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;

public class AnswerOptionDslModelMother {

    public static AnswerOptionDslModel answerOptionDslModel(Integer index, String caption, Integer value) {
        AnswerOptionDslModel dslAnswerOption = new AnswerOptionDslModel();
        dslAnswerOption.setIndex(index);
        dslAnswerOption.setCaption(caption);
        dslAnswerOption.setValue(value);
        return dslAnswerOption;
    }
}
