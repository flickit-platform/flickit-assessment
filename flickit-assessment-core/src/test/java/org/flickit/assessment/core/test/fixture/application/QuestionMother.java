package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.domain.Question;

import java.util.List;

public class QuestionMother {

    private static long id = 134L;
    private static int index = 1;

    public static Question withImpactsOnLevel2(long attributeId) {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelTwo(1, attributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withImpactsOnLevel3(long attributeId) {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelThree(1, attributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withImpactsOnLevel23(long attributeId) {
        return new Question(id++, "question" + id, index++, "hint", Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelTwo(1, attributeId), QuestionImpactMother.onLevelThree(1, attributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withImpactsOnLevel23WithWeights(long attributeId, int level2ImpactWeight, int level3ImpactWeight) {
        return new Question(id++, "question" + id, index++, "hint", Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelTwo(level2ImpactWeight, attributeId), QuestionImpactMother.onLevelThree(level3ImpactWeight, attributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withImpactsOnLevel24(long attributeId) {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelTwo(1, attributeId), QuestionImpactMother.onLevelFour(1, attributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withImpactsOnLevel34(long attributeId) {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelThree(1, attributeId), QuestionImpactMother.onLevelFour(1, attributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withImpactsOnLevel45(long attributeId) {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelFour(1, attributeId), QuestionImpactMother.onLevelFive(1, attributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withNoImpact() {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE, null,
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withOptions() {
        Question question = new Question(id++, "question" + id, index++, null, Boolean.FALSE, null,
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
        question.setOptions(List.of(AnswerOptionMother.optionOne(), AnswerOptionMother.optionFour()));
        return question;
    }

    public static Question withImpactsOnLevel3AndAnotherAttributeLevel4(long attributeId, long anotherAttributeId) {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelThree(1, attributeId), QuestionImpactMother.onLevelFour(1, anotherAttributeId)),
            QuestionnaireMother.createQuestionnaire(),
            MeasureMother.createMeasure());
    }

    public static Question withMeasure(Measure measure) {
        return new Question(id++, "question" + id, index++, null, Boolean.FALSE,
            List.of(QuestionImpactMother.onLevelTwo(1, 15L)),
            QuestionnaireMother.createQuestionnaire(),
            measure);
    }
}
