package org.flickit.flickitassessmentcore.domain.calculate;

import org.flickit.flickitassessmentcore.domain.calculate.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.QualityAttributeValueMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.SubjectValueMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubjectValueTest {

    @Test
    void calculate_withSameWeightsAndLevels() {

        List<QualityAttributeValue> qualityAttributeValues = List.of(
            QualityAttributeValueMother.levelThreeWithWeight(1),
            QualityAttributeValueMother.levelThreeWithWeight(1),
            QualityAttributeValueMother.levelThreeWithWeight(1),
            QualityAttributeValueMother.levelThreeWithWeight(1),
            QualityAttributeValueMother.levelThreeWithWeight(1));


        SubjectValue subjectValue = SubjectValueMother.builder()
            .qualityAttributeValues(qualityAttributeValues)
            .build();

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getLevel(), subjectMaturityLevel.getLevel());
    }

    @Test
    void calculate_withDifferentWeightsAndLevels() {
        List<QualityAttributeValue> qualityAttributeValues = List.of(
            QualityAttributeValueMother.levelFourWithWeight(1),
            QualityAttributeValueMother.levelFourWithWeight(2),
            QualityAttributeValueMother.levelThreeWithWeight(10),
            QualityAttributeValueMother.levelFourWithWeight(2),
            QualityAttributeValueMother.levelFourWithWeight(1));

        SubjectValue subjectValue = SubjectValueMother.builder()
            .qualityAttributeValues(qualityAttributeValues)
            .build();

        MaturityLevel subjectMaturityLevel = subjectValue.calculate(MaturityLevelMother.allLevels());

        assertEquals(MaturityLevelMother.levelThree().getLevel(), subjectMaturityLevel.getLevel());
    }
}
