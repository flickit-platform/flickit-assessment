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

        assertEquals(MaturityLevelMother.levelThree().getLevel(), assessmentMaturityLevel.getLevel());
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

        assertEquals(MaturityLevelMother.levelThree().getLevel(), assessmentMaturityLevel.getLevel());
    }

    @Test
    void testCalculateConfidenceLevel_withOneSample() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelFourLimitedQuestionWithWeight(1)
        )));

        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        double calculateConfidenceLevel = assessmentResult.calculateConfidenceLevel();

        assertEquals(2.0, calculateConfidenceLevel);
    }

    @Test
    void testCalculateConfidenceLevel_withSameWeights() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(SubjectValueMother.withQAValues(List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelFourWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelFourWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelFourWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelFourWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelFourWithWeight(1)
        )));

        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        double calculateConfidenceLevel = assessmentResult.calculateConfidenceLevel();

        assertEquals(1.6666666666666667, calculateConfidenceLevel);
    }

}
