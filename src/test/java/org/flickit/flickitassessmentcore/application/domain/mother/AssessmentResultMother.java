package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public class AssessmentResultMother {

    public static AssessmentResult invalidResultWithSubjectValues(List<SubjectValue> subjectValues) {
        return new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), subjectValues);
    }
}
