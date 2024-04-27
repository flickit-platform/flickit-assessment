package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AssessmentKit;

import java.util.UUID;

public interface LoadExpertGroupAllKitListPort {

    PaginatedResponse<AssessmentKit> loadAllKitsByKitIdAndUserId(Long expertGroupId, UUID userId, int page, int size);
}
