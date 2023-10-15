package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.domain.crud.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;

import java.util.List;

public interface LoadAssessmentListItemsBySpacePort {

    PaginatedResponse<AssessmentListItem> loadNotDeletedAssessments(List<Long> spaceIds, Long kitId, int page, int size);
}
