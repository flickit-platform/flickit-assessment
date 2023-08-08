package org.flickit.flickitassessmentcore.domain.calculate;

import org.flickit.flickitassessmentcore.domain.calculate.mother.AssessmentResultMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.QualityAttributeValueMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.SubjectValueMother;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentResultTest {

    @Test
    void calculate_withSameWeightsAndLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1)
        )));


        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        assertEquals(MaturityLevelMother.levelThree().getLevel(), assessmentMaturityLevel.getLevel());
    }

    @Test
    void calculate_withDifferentWeightsAndLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(10),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(1)
        )));


        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        assertEquals(MaturityLevelMother.levelThree().getLevel(), assessmentMaturityLevel.getLevel());
    }
}
