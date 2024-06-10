package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.AttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;

import java.util.List;

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
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()));

        SubjectValue subjectValue = SubjectValueMother.withQAValues(attributeValues);

        double calculatedConfidenceValue = subjectValue.calculateConfidenceValue();

        assertEquals(80.0, calculatedConfidenceValue);
    }

    @Test
    void testCalculateConfidenceLevel_withDifferentWeightsAndConfidenceLevels() {
        List<AttributeValue> attributeValues = List.of(
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.FAIRLY_UNSURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(5, ConfidenceLevel.COMPLETELY_SURE.getId()));

        SubjectValue subjectValue = SubjectValueMother.withQAValues(attributeValues);

        double calculatedConfidenceValue = subjectValue.calculateConfidenceValue();

        assertEquals(73.333333333333333, calculatedConfidenceValue);
    }

}
