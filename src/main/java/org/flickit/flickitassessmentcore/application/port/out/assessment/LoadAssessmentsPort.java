package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase.AssessmentListItem;

import java.util.List;
import java.util.UUID;

public interface LoadAssessmentsPort {

    List<AssessmentListItem> load(List<UUID> assessmentIds);
}
