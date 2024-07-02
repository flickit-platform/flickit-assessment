package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.AttributeValueMother;
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
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(1)
        )));


        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        assertEquals(MaturityLevelMother.levelThree().getValue(), assessmentMaturityLevel.getValue());
    }

    @Test
    void testCalculate_withDifferentWeightsAndLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(1),
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            AttributeValueMother.toBeCalcAsLevelThreeWithWeight(10),
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            AttributeValueMother.toBeCalcAsLevelFourWithWeight(1)
        )));


        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        assertEquals(MaturityLevelMother.levelThree().getValue(), assessmentMaturityLevel.getValue());
    }

    @Test
    void testCalculateConfidenceLevel_withSameWeightsAndConfidenceLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId())
        )));

        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        double calculateConfidenceLevel = assessmentResult.calculateConfidenceValue();

        assertEquals(80.0, calculateConfidenceLevel);
    }

    @Test
    void testCalculateConfidenceLevel_withDifferentWeightsAndConfidenceLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.FAIRLY_UNSURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.FAIRLY_SURE.getId()),
            AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(5, ConfidenceLevel.COMPLETELY_SURE.getId())
        )));

        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        double calculateConfidenceLevel = assessmentResult.calculateConfidenceValue();

        assertEquals(73.333333333333333, calculateConfidenceLevel);
    }

}
