package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.domain.crud.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;

import java.util.List;

public interface LoadAssessmentListItemsBySpacePort {

    PaginatedResponse<AssessmentListItem> loadAssessments(List<Long> spaceIds, Long kitId, Long deletionTime, int page, int size);
}
