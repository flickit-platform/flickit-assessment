package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentListItem;

import java.util.UUID;

public interface LoadAssessmentListPort {

    PaginatedResponse<AssessmentListItem> loadUserAssessments(Long kitId, UUID userId, int page, int size);

    PaginatedResponse<AssessmentListItem> loadSpaceAssessments(Long spaceId, UUID userId, int page, int size);
}
