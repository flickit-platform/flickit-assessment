package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;

public class QuestionMother {

    private static long id = 134L;

    public static Question withImpactsOnLevel23(long attributeId) {
        return new Question(id++, "question" + id, null, null, Boolean.FALSE, List.of(QuestionImpactMother.onLevelTwo(1, attributeId), QuestionImpactMother.onLevelThree(1, attributeId)));
    }

    public static Question withImpactsOnLevel24(long attributeId) {
        return new Question(id++, "question" + id, null, null, Boolean.FALSE, List.of(QuestionImpactMother.onLevelTwo(1, attributeId), QuestionImpactMother.onLevelFour(1, attributeId)));
    }

    public static Question withImpactsOnLevel34(long attributeId) {
        return new Question(id++, "question" + id, null, null, Boolean.FALSE, List.of(QuestionImpactMother.onLevelThree(1, attributeId), QuestionImpactMother.onLevelFour(1, attributeId)));
    }

    public static Question withImpactsOnLevel45(long attributeId) {
        return new Question(id++, "question" + id, null, null, Boolean.FALSE, List.of(QuestionImpactMother.onLevelFour(1, attributeId), QuestionImpactMother.onLevelFive(1, attributeId)));
    }

    public static Question withNoImpact() {
        return new Question(id++, "question" + id, null, null, Boolean.FALSE, null);
    }

    public static Question withOptions() {
        Question question = new Question(id++, "question" + id, null, null, Boolean.FALSE, null);
        Attribute attribute = AttributeMother.simpleAttribute();
        question.setOptions(List.of(AnswerOptionMother.optionOne(attribute.getId()), AnswerOptionMother.optionTwo(attribute.getId())));
        return question;
    }

    public static Question withIdAndImpactsOnLevel23(long id, long attributeId) {
        return new Question(id, "question" + id, null, null, Boolean.FALSE, List.of(QuestionImpactMother.onLevelTwo(1, attributeId), QuestionImpactMother.onLevelThree(1, attributeId)));
    }
}
