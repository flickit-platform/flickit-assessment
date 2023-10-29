package org.flickit.flickitassessmentcore.application.domain;

import org.flickit.flickitassessmentcore.test.fixture.application.MaturityLevelMother;
import org.flickit.flickitassessmentcore.test.fixture.application.QualityAttributeValueMother;
import org.flickit.flickitassessmentcore.test.fixture.application.SubjectValueMother;
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

        assertEquals(MaturityLevelMother.levelThree().getLevel(), subjectMaturityLevel.getLevel());
    }

    @Test
    void calculate_withDifferentWeightsAndLevels() {
        List<QualityAttributeValue> qualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(10),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(1));

        SubjectValue subjectValue = SubjectValueMother.withQAValues(qualityAttributeValues);

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getLevel(), subjectMaturityLevel.getLevel());
    }
}
