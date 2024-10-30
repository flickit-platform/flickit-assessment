package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.*;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withAttributeValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectValueTest {

    @Test
    void testCalculate_withSameWeightsAndScores() {

        List<AttributeValue> attributeValues = List.of(
            hasFullScoreOnLevel23WithWeight(1),
            hasFullScoreOnLevel23WithWeight(1),
            hasFullScoreOnLevel23WithWeight(1),
            hasFullScoreOnLevel23WithWeight(1),
            hasFullScoreOnLevel23WithWeight(1));


        SubjectValue subjectValue = withAttributeValues(attributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(allLevels());
        assertEquals(MaturityLevelMother.levelThree().getValue(), subjectMaturityLevel.getValue());
    }

    @Test
    void testCalculate_withDifferentWeightsAndScores() {
        var attributeValues = List.of(
            hasFullScoreOnLevel24WithWeight(1),
            hasFullScoreOnLevel24WithWeight(2),
            hasFullScoreOnLevel23WithWeight(10),
            hasFullScoreOnLevel24WithWeight(2),
            hasFullScoreOnLevel24WithWeight(1));

        SubjectValue subjectValue = withAttributeValues(attributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(allLevels());
        assertEquals(MaturityLevelMother.levelThree().getValue(), subjectMaturityLevel.getValue());
    }

    @Test
    void testCalculate_withDifferentWeightsAndScores_differentResultWithWeightedMeanLevelAndWeightedMeanScoreAlgorithms() {
        var av1 = hasFullScoreOnLevel23WithWeight(1);
        var av2 = hasFullScoreOnLevel23WithWeight(1);
        var av3 = hasPartialScoreOnLevel2AndFullScoreOnLevel3WithWeight(10);

        var attributeValues = List.of(av1, av2, av3);

        SubjectValue subjectValue = withAttributeValues(attributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(allLevels());
        assertEquals(levelThree().getValue(), av1.getMaturityLevel().getValue());
        assertEquals(levelThree().getValue(), av2.getMaturityLevel().getValue());
        assertEquals(levelTwo().getValue(), av3.getMaturityLevel().getValue());

        // WeightedMeanLevel is equal to level two
        assertEquals(levelTwo().getValue(), Math.round((double) ((av1.getWeightedLevel() + av2.getWeightedLevel() + av3.getWeightedLevel()) / 12)));
        Map<UUID, Double> map = Map.of(
            av1.getId(), av1.getWeightedScore().get(levelTwo().getId()),
            av2.getId(), av2.getWeightedScore().get(levelTwo().getId()),
            av3.getId(), av3.getWeightedScore().get(levelTwo().getId()));

        // WeightedMeanScore of attributes on level two is 75 which passes the competence of level three
        assertEquals(75.0, (map.get(av1.getId()) + map.get(av2.getId()) + map.get(av3.getId())) / 12);
        assertEquals(MaturityLevelMother.levelThree().getValue(), subjectMaturityLevel.getValue());
    }

    @Test
    void testCalculateConfidenceLevel_withSameWeightsAndConfidenceLevels() {
        List<AttributeValue> attributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()), //6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()), //6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()), //6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()), //6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()));//6 questions with 5 answers with cl=4, attrCl=20/30

        SubjectValue subjectValue = withAttributeValues(attributeValues);

        double calculatedConfidenceValue = subjectValue.calculateConfidenceValue();

        double maxPossibleSumConfidence = 100 * 5;
        double gainedSumConfidence = (((20.0 / 30.0) * 1) * 5) * 100;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, calculatedConfidenceValue, 0.01);
    }

    @Test
    void testCalculateConfidenceLevel_withDifferentWeightsAndConfidenceLevels() {
        List<AttributeValue> attributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_UNSURE.getId()), //6 questions with 5 answers with cl=1, attrCl=5/30
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.FAIRLY_UNSURE.getId()),     //6 questions with 5 answers with cl=2, attrCl = 10/30
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),   //6 questions with 5 answers with cl=3, attrCl = 15/30
            toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.FAIRLY_SURE.getId()),       //6 questions with 5 answers with cl=4, attrCl = 20/30
            toBeCalcAsConfidenceLevelWithWeight(5, ConfidenceLevel.COMPLETELY_SURE.getId())    //6 questions with 5 answers with cl=5, attrCl = 25/30
        );

        SubjectValue subjectValue = withAttributeValues(attributeValues);

        double calculatedConfidenceValue = subjectValue.calculateConfidenceValue();

        double maxPossibleSumConfidence = (100 * 1) + (100 * 2) + (100 * 3) + (100 * 4) + (100 * 5);
        double gainedSumConfidence = (((5.0 / 30.0) * 1) + ((10.0 / 30.0) * 2) + ((15.0 / 30.0) * 3) +
            ((20.0 / 30.0) * 4) + ((25.0 / 30.0) * 5)) * 100;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, calculatedConfidenceValue, 0.01);
    }
}
