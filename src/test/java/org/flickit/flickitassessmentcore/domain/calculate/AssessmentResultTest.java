package org.flickit.flickitassessmentcore.domain.calculate;

import org.flickit.flickitassessmentcore.domain.calculate.mother.AssessmentResultMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.QualityAttributeValueMother;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentResultTest {

    @Test
    void calculate_withSameWeightsAndLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValue.builder()
            .qualityAttributeValues(List.of(
                QualityAttributeValueMother.levelThreeWithWeight(1),
                QualityAttributeValueMother.levelThreeWithWeight(1),
                QualityAttributeValueMother.levelThreeWithWeight(1),
                QualityAttributeValueMother.levelThreeWithWeight(1),
                QualityAttributeValueMother.levelThreeWithWeight(1)
            ))
            .build());


        AssessmentResult assessmentResult = AssessmentResultMother.builder()
            .subjectValues(subjectValues)
            .build();

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        assertEquals(MaturityLevelMother.levelThree().getLevel(), assessmentMaturityLevel.getLevel());
    }

    @Test
    void calculate_withDifferentWeightsAndLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValue.builder()
            .qualityAttributeValues(List.of(
                QualityAttributeValueMother.levelFourWithWeight(1),
                QualityAttributeValueMother.levelFourWithWeight(2),
                QualityAttributeValueMother.levelThreeWithWeight(10),
                QualityAttributeValueMother.levelFourWithWeight(2),
                QualityAttributeValueMother.levelFourWithWeight(1)
            ))
            .build());


        AssessmentResult assessmentResult = AssessmentResultMother.builder()
            .subjectValues(subjectValues)
            .build();

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        assertEquals(MaturityLevelMother.levelThree().getLevel(), assessmentMaturityLevel.getLevel());
    }
}
