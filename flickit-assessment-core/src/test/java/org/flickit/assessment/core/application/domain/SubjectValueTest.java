package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectValueTest {

    @Test
    void testCalculate_withSameWeightsAndLevels() {

        List<QualityAttributeValue> qualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1));


        SubjectValue subjectValue = SubjectValueMother.withQAValues(qualityAttributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getValue(), subjectMaturityLevel.getValue());
    }

    @Test
    void testCalculate_withDifferentWeightsAndLevels() {
        List<QualityAttributeValue> qualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(10),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(1));

        SubjectValue subjectValue = SubjectValueMother.withQAValues(qualityAttributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getValue(), subjectMaturityLevel.getValue());
    }

    @Test
    void testCalculateConfidenceLevel_withSameWeightsAndConfidenceLevels() {
        List<QualityAttributeValue> qualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()));

        SubjectValue subjectValue = SubjectValueMother.withQAValues(qualityAttributeValues);

        double calculatedConfidenceValue = subjectValue.calculateConfidenceValue();

        assertEquals(80.0, calculatedConfidenceValue);
    }

    @Test
    void testCalculateConfidenceLevel_withDifferentWeightsAndConfidenceLevels() {
        List<QualityAttributeValue> qualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.FAIRLY_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(5, ConfidenceLevel.COMPLETELY_SURE.getId()));

        SubjectValue subjectValue = SubjectValueMother.withQAValues(qualityAttributeValues);

        double calculatedConfidenceValue = subjectValue.calculateConfidenceValue();

        assertEquals(73.333333333333333, calculatedConfidenceValue);
    }

}
