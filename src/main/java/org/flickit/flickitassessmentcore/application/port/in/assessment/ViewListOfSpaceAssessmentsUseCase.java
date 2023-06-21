package org.flickit.flickitassessmentcore.application.port.in.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.List;

public interface ViewListOfSpaceAssessmentsUseCase {

    public List<Assessment> viewListOfSpaceAssessments(ViewListOfSpaceAssessmentsCommand command);
}
