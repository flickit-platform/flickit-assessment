package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.AttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectValueTest {

    @Test
    void testCalculate_withSameWeightsAndLevels() {

        List<AttributeValue> attributeValues = List.of(
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1));


        SubjectValue subjectValue = SubjectValueMother.withQAValues(attributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getValue(), subjectMaturityLevel.getValue());
    }

    @Test
    void testCalculate_withDifferentWeightsAndLevels() {
        List<AttributeValue> attributeValues = List.of(
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(10),
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(1));

        SubjectValue subjectValue = SubjectValueMother.withQAValues(attributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(MaturityLevelMother.allLevels());

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

        SubjectValue subjectValue = SubjectValueMother.withQAValues(attributeValues);

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

        SubjectValue subjectValue = SubjectValueMother.withQAValues(attributeValues);

        double calculatedConfidenceValue = subjectValue.calculateConfidenceValue();

        double maxPossibleSumConfidence = (100 * 1) + (100 * 2) + (100 * 3) + (100 * 4) + (100 * 5);
        double gainedSumConfidence = (((5.0 / 30.0) * 1) + ((10.0 / 30.0) * 2) + ((15.0 / 30.0) * 3) +
            ((20.0 / 30.0) * 4) + ((25.0 / 30.0) * 5)) * 100;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, calculatedConfidenceValue, 0.01);

    }

}
