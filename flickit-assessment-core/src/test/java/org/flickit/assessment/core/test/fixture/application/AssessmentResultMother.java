package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssessmentResultMother {

    public static AssessmentResult invalidResultWithSubjectValues(List<SubjectValue> subjectValues) {
        Assessment assessment = AssessmentMother.assessment();
        return new AssessmentResult(UUID.randomUUID(), assessment, assessment.getAssessmentKit().getKitVersion(),
            subjectValues, LocalDateTime.now(), LocalDateTime.now());
    }

    public static AssessmentResult validResultWithSubjectValuesAndMaturityLevel(List<SubjectValue> subjectValues, MaturityLevel maturityLevel) {
        Assessment assessment = AssessmentMother.assessment();
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), assessment, assessment.getAssessmentKit().getKitVersion(),
            subjectValues, LocalDateTime.now(), LocalDateTime.now());
        assessmentResult.setIsCalculateValid(true);
        assessmentResult.setMaturityLevel(maturityLevel);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        assessmentResult.setLastConfidenceCalculationTime(LocalDateTime.now());
        return assessmentResult;
    }

    public static AssessmentResult validResultWithSubjectValuesAndMaturityLevelAndConfidenceValue(List<SubjectValue> subjectValues, MaturityLevel maturityLevel, double confidenceValue) {
        Assessment assessment = AssessmentMother.assessment();
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), assessment, assessment.getAssessmentKit().getKitVersion(),
            subjectValues, LocalDateTime.now(), LocalDateTime.now());
        assessmentResult.setIsCalculateValid(true);
        assessmentResult.setMaturityLevel(maturityLevel);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        assessmentResult.setLastConfidenceCalculationTime(LocalDateTime.now());
        assessmentResult.setConfidenceValue(confidenceValue);
        return assessmentResult;
    }

    public static AssessmentResult validResultWithJustAnId() {
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), 123L, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        assessmentResult.setIsCalculateValid(true);
        assessmentResult.setIsConfidenceValid(true);
        return assessmentResult;
    }

    public static AssessmentResult validResultWithoutActiveVersion() {
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessmentWithoutActiveVersion(), 123L, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        assessmentResult.setIsCalculateValid(true);
        assessmentResult.setIsConfidenceValid(true);
        return assessmentResult;
    }

    public static AssessmentResult resultWithValidations(Boolean isCalculateValid, Boolean isConfCalculationValid,
                                                         LocalDateTime lastCalculationTime, LocalDateTime lastConfCalculationTime) {
        Assessment assessment = AssessmentMother.assessment();
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(),assessment, assessment.getAssessmentKit().getKitVersion(),
            List.of(), LocalDateTime.now(), LocalDateTime.now());
        assessmentResult.setIsCalculateValid(isCalculateValid);
        assessmentResult.setIsConfidenceValid(isConfCalculationValid);
        assessmentResult.setLastCalculationTime(lastCalculationTime);
        assessmentResult.setLastConfidenceCalculationTime(lastConfCalculationTime);
        return assessmentResult;
    }

    public static AssessmentResult resultWithDeprecatedKitVersion(Boolean isCalculateValid, Boolean isConfCalculationValid,
                                                                  LocalDateTime lastCalculationTime, LocalDateTime lastConfCalculationTime) {
        Assessment assessment = AssessmentMother.assessment();
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), assessment, assessment.getAssessmentKit().getKitVersion() - 1,
            List.of(), LocalDateTime.now(), LocalDateTime.now());
        assessmentResult.setIsCalculateValid(isCalculateValid);
        assessmentResult.setIsConfidenceValid(isConfCalculationValid);
        assessmentResult.setLastCalculationTime(lastCalculationTime);
        assessmentResult.setLastConfidenceCalculationTime(lastConfCalculationTime);
        return assessmentResult;
    }
}
