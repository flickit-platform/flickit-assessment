package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.*;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withAttributeValues;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentResultTest {

    @Test
    void testCalculate_withSameWeightsAndLevels() {
        SubjectValue sv1 = withAttributeValues(List.of(
            hasFullScoreOnLevel23WithWeight(1, 1533),
            hasFullScoreOnLevel23WithWeight(1, 1534)
        ), 3);
        SubjectValue sv2 = withAttributeValues(List.of(
            hasFullScoreOnLevel23WithWeight(1, 1535),
            hasFullScoreOnLevel23WithWeight(1, 1536),
            hasFullScoreOnLevel23WithWeight(1, 1537)
        ), 3);
        List<SubjectValue> subjectValues = List.of(sv1, sv2);

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        assertEquals(levelThree().getValue(), assessmentMaturityLevel.getValue());
    }

    @Test
    void testCalculate_withSameWeightsAndDifferentLevels() {
        SubjectValue sv1 = withAttributeValues(List.of(
            hasFullScoreOnLevel23WithWeight(1, 1533),
            hasFullScoreOnLevel23WithWeight(1, 1534)
        ), 5);
        SubjectValue sv2 = withAttributeValues(List.of(
            hasFullScoreOnLevel24WithWeight(10, 1535),
            hasFullScoreOnLevel24WithWeight(10, 1536),
            hasFullScoreOnLevel24WithWeight(10, 1537)
        ), 3);
        List<SubjectValue> subjectValues = List.of(sv1, sv2);

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);

        MaturityLevel assessmentMaturityLevel = assessmentResult.calculate();

        //level2 score = 100, level3 score = (5*100 + 3*0)/8 = 62.5 => so level three passes
        assertEquals(levelThree().getValue(), assessmentMaturityLevel.getValue());

        sv1 = withAttributeValues(List.of(
            hasFullScoreOnLevel23WithWeight(1, 1533),
            hasFullScoreOnLevel23WithWeight(1, 1534)
        ), 4);
        sv2 = withAttributeValues(List.of(
            hasFullScoreOnLevel24WithWeight(10, 1535),
            hasFullScoreOnLevel24WithWeight(10, 1536),
            hasFullScoreOnLevel24WithWeight(10, 1537)
        ), 3);
        subjectValues = List.of(sv1, sv2);

        assessmentResult = invalidResultWithSubjectValues(subjectValues);

        assessmentMaturityLevel = assessmentResult.calculate();

        //level2 score = 100, level3 score = (4*100 + 3*0)/7 = 57.5 => so level three does not passe
        assertEquals(levelTwo().getValue(), assessmentMaturityLevel.getValue());
    }

    @Test
    void testCalculateConfidenceLevel_withSameWeightsAndConfidenceLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(withAttributeValues(List.of(
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),//6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),//6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),//6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()),//6 questions with 5 answers with cl=4, attrCl=20/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.FAIRLY_SURE.getId()) //6 questions with 5 answers with cl=4, attrCl=20/30
        ), 1));

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);

        double calculatedConfidenceValue = assessmentResult.calculateConfidenceValue();

        double maxPossibleSumConfidence = 100 * 5;
        double gainedSumConfidence = (((20.0 / 30.0) * 1) * 5) * 100;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, calculatedConfidenceValue, 0.01);
    }

    @Test
    void testCalculateConfidenceLevel_withDifferentWeightsAndConfidenceLevels() {
        List<SubjectValue> subjectValues = new ArrayList<>();
        subjectValues.add(withAttributeValues(List.of(
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_UNSURE.getId()),//6 questions with 5 answers with cl=1, attrCl=5/30
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.FAIRLY_UNSURE.getId()),//6 questions with 5 answers with cl=2, attrCl = 10/30
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),//6 questions with 5 answers with cl=3, attrCl = 15/30
            toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.FAIRLY_SURE.getId()),//6 questions with 5 answers with cl=4, attrCl = 20/30
            toBeCalcAsConfidenceLevelWithWeight(5, ConfidenceLevel.COMPLETELY_SURE.getId())//6 questions with 5 answers with cl=5, attrCl = 25/30
        ), 1));

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);

        double calculatedConfidenceValue = assessmentResult.calculateConfidenceValue();

        double maxPossibleSumConfidence = (100 * 1) + (100 * 2) + (100 * 3) + (100 * 4) + (100 * 5);
        double gainedSumConfidence = (((5.0 / 30.0) * 1) + ((10.0 / 30.0) * 2) + ((15.0 / 30.0) * 3) +
            ((20.0 / 30.0) * 4) + ((25.0 / 30.0) * 5)) * 100;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, calculatedConfidenceValue, 0.01);
    }
}
