package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentWithMaturityLevelId;

import java.util.List;

public interface LoadAssessmentsWithMaturityLevelIdBySpacePort {

    List<AssessmentWithMaturityLevelId> loadAssessmentsWithLastResultMaturityLevelIdBySpaceId(Long spaceId, int page, int size);
}
