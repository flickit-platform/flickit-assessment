package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AssessmentResultMother {

    public static AssessmentResult invalidResultWithSubjectValues(List<SubjectValue> subjectValues) {
        return new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), subjectValues);
    }

    public static AssessmentResult validResultWithSubjectValuesAndMaturityLevel(List<SubjectValue> subjectValues, MaturityLevel maturityLevel) {
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), subjectValues);
        assessmentResult.setCalculateValid(true);
        assessmentResult.setMaturityLevel(maturityLevel);
        return assessmentResult;
    }

    public static AssessmentResult validResultWithJustAnId() {
        AssessmentResult assessmentResult = new AssessmentResult(UUID.randomUUID(), null, new ArrayList<>());
        assessmentResult.setCalculateValid(true);
        return assessmentResult;
    }
}
