package org.flickit.flickitassessmentcore.application.port.in.assessmentsubject;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;

public interface CalculateSubjectMaturityLevelUseCase {

    MaturityLevel calculateSubjectMaturityLevel(CalculateSubjectMaturityLevelCommand command);
}
