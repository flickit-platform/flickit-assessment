package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.calculate.SubjectValue;

import java.util.List;
import java.util.UUID;

public class AssessmentResultMother {

    public static AssessmentResult invalidResultWithSubjectValues(List<SubjectValue> subjectValues) {
        return new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), subjectValues);
    }
}
