package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Answer;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;

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

    public static QualityAttributeValue withAttributeAndMaturityLevel(QualityAttribute attribute, MaturityLevel maturityLevel) {
        QualityAttributeValue attributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            attribute,
            null);

        attributeValue.setMaturityLevel(maturityLevel);
        return attributeValue;
    }
}
