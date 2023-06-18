package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import org.flickit.flickitassessmentcore.domain.AssessmentSubject;
import org.flickit.flickitassessmentcore.domain.AssessmentSubjectValue;

public interface CalculateAssessmentSubjectMaturityLevelUseCase {

    public AssessmentSubjectValue calculateAssessmentSubjectMaturityLevel(AssessmentSubject subject);
}
