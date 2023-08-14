package org.flickit.flickitassessmentcore.domain.mother;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public class AssessmentResultMother {

    public static AssessmentResult invalidResultWithSubjectValues(List<SubjectValue> subjectValues) {
        return new AssessmentResult(UUID.randomUUID(), AssessmentMother.assessment(), subjectValues);
    }
}
