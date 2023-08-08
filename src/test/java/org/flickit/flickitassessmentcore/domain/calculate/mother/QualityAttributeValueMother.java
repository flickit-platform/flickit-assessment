package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.Answer;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

import java.util.List;
import java.util.UUID;

public class QualityAttributeValueMother {

    public static QualityAttributeValue toBeCalcWithQAAndAnswers(QualityAttribute qualityAttribute, List<Answer> answers) {
        return new QualityAttributeValue(UUID.randomUUID(), qualityAttribute, answers);
    }

    public static QualityAttributeValue toBeCalcAsLevelThreeWithWeight(int weight) {
        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23());

        return new QualityAttributeValue(UUID.randomUUID(),
            QualityAttributeMother.withQuestionsOnLevel23(weight),
            answers);
    }

    public static QualityAttributeValue toBeCalcAsLevelFourWithWeight(int weight) {
        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24());

        return new QualityAttributeValue(UUID.randomUUID(),
            QualityAttributeMother.withQuestionsOnLevel24(weight),
            answers);
    }
}
