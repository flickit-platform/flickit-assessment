package org.flickit.flickitassessmentcore.domain;

import org.flickit.flickitassessmentcore.domain.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.domain.mother.QualityAttributeValueMother;
import org.flickit.flickitassessmentcore.domain.mother.SubjectValueMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubjectValueTest {

    @Test
    void calculate_withSameWeightsAndLevels() {

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
