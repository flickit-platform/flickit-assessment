package org.flickit.flickitassessmentcore.application.port.in.assessmentresult;

import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentSubjectValue;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.List;

public interface CalculateAssessmentMaturityLevelUseCase {
    public MaturityLevel calculateAssessmentMaturityLevel(List<AssessmentSubjectValue> subjectValues, Assessment assessment);

}
