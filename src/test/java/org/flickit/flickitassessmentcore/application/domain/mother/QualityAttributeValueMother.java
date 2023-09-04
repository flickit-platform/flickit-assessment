package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Answer;
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

    public static QualityAttributeValue calcAsLevelOneWithAttribute(QualityAttribute attribute) {
        QualityAttributeValue attributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            attribute,
            null);

        attributeValue.setMaturityLevel(MaturityLevelMother.levelOne());
        return attributeValue;
    }

    public static QualityAttributeValue calcAsLevelTwoWithAttribute(QualityAttribute attribute) {
        QualityAttributeValue attributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            attribute,
            null);

        attributeValue.setMaturityLevel(MaturityLevelMother.levelTwo());
        return attributeValue;
    }

    public static QualityAttributeValue calcAsLevelThreeWithAttribute(QualityAttribute attribute) {
        QualityAttributeValue attributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            attribute,
            null);

        attributeValue.setMaturityLevel(MaturityLevelMother.levelThree());
        return attributeValue;
    }

    public static QualityAttributeValue calcAsLevelFourWithAttribute(QualityAttribute attribute) {
        QualityAttributeValue attributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            attribute,
            null);

        attributeValue.setMaturityLevel(MaturityLevelMother.levelFour());
        return attributeValue;
    }

    public static QualityAttributeValue calcAsLevelFiveWithAttribute(QualityAttribute attribute) {
        QualityAttributeValue attributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            attribute,
            null);

        attributeValue.setMaturityLevel(MaturityLevelMother.levelFive());
        return attributeValue;
    }
}
