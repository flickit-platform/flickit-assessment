package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase.ComparableAssessmentListItem;

import java.util.List;
import java.util.UUID;

public interface LoadAssessmentsPort {

    List<ComparableAssessmentListItem> load(List<UUID> assessmentIds);
}
