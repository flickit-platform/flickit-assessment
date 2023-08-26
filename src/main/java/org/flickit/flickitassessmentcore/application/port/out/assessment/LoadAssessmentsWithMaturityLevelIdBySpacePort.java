package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;

import java.util.List;

public interface LoadAssessmentsWithMaturityLevelIdBySpacePort {

    List<AssessmentListItem> loadAssessmentListItemBySpaceId(Long spaceId, int page, int size);
}
