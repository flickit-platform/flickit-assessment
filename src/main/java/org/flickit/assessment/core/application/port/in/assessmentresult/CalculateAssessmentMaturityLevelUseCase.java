package org.flickit.assessment.core.application.port.in.assessmentresult;

import org.flickit.assessment.core.domain.Assessment;
import org.flickit.assessment.core.domain.AssessmentSubjectValue;
import org.flickit.assessment.core.domain.MaturityLevel;

import java.util.List;

public interface CalculateAssessmentMaturityLevelUseCase {
    public MaturityLevel calculateAssessmentMaturityLevel(List<AssessmentSubjectValue> subjectValues, Assessment assessment);

}
