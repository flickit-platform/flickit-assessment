package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentResultTest {

    @Test
    void testCalculate_withSameWeightsAndLevels() {
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

        assertEquals(MaturityLevelMother.levelThree().getValue(), assessmentMaturityLevel.getValue());
    }

    @Test
    void testCalculate_withDifferentWeightsAndLevels() {
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

        assertEquals(MaturityLevelMother.levelThree().getValue(), assessmentMaturityLevel.getValue());
    }

    @Test
    void testCalculateConfidenceLevel_withSameWeightsAndConfidenceLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId())
        )));

        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        double calculateConfidenceLevel = assessmentResult.calculateConfidenceValue();

        assertEquals(80.0, calculateConfidenceLevel);
    }

    @Test
    void testCalculateConfidenceLevel_withDifferentWeightsAndConfidenceLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.FAIRLY_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.FAIRLY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(5, ConfidenceLevel.COMPLETELY_SURE.getId())
        )));

        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        double calculateConfidenceLevel = assessmentResult.calculateConfidenceValue();

        assertEquals(73.333333333333333, calculateConfidenceLevel);
    }

}
