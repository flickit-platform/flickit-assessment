package org.flickit.assessment.core.application.port.in.assessmentresult;

import org.flickit.assessment.core.domain.AssessmentSubject;
import org.flickit.assessment.core.domain.AssessmentSubjectValue;

public interface CalculateAssessmentSubjectMaturityLevelUseCase {

    public AssessmentSubjectValue calculateAssessmentSubjectMaturityLevel(AssessmentSubject subject);
}
