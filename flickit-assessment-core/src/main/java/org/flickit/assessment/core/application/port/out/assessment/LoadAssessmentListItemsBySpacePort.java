package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;

import java.util.List;

public interface LoadAssessmentListItemsBySpacePort {

    PaginatedResponse<AssessmentListItem> loadNotDeletedAssessments(List<Long> spaceIds, Long kitId, int page, int size);
}
