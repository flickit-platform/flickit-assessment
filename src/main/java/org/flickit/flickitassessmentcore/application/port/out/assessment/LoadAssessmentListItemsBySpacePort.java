package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;

public interface LoadAssessmentListItemsBySpacePort {

    PaginatedResponse<AssessmentListItem> loadAssessments(Long spaceId, int page, int size);
}
