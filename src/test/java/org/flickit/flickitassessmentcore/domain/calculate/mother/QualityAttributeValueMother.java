package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.*;

import java.util.List;
import java.util.UUID;

public class QualityAttributeValueMother {

    public static QualityAttributeValue.QualityAttributeValueBuilder builder() {
        return QualityAttributeValue.builder()
            .id(UUID.randomUUID());
    }

    public static QualityAttributeValue withWeightAndLevel(int weight, int level) {
        return QualityAttributeValue.builder()
            .qualityAttribute(QualityAttributeMother.withWeight(weight))
            .maturityLevel(MaturityLevelMother.withLevel(level))
            .build();
    }

    public static QualityAttributeValue levelThreeWithWeight(int weight) {
        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23(),
            AnswerMother.fullScoreOnLevels23());

        return QualityAttributeValueMother.builder()
            .qualityAttribute(QualityAttributeMother.builderWithQuestionsOnLevel23()
                .weight(weight).build())
            .answers(answers)
            .build();
    }

    public static QualityAttributeValue levelFourWithWeight(int weight) {
        List<Answer> answers = List.of(
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24(),
            AnswerMother.fullScoreOnLevels24());

        return QualityAttributeValueMother.builder()
            .qualityAttribute(QualityAttributeMother.builderWithQuestionsOnLevel24()
                .weight(weight).build())
            .answers(answers)
            .build();
    }
}
