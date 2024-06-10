package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AssessmentKit;

import java.util.UUID;

public interface SearchKitOptionsPort {

    PaginatedResponse<AssessmentKit> searchKitOptions(Param param);

    record Param(String query, int page, int size, UUID currentUserId) {}
}
