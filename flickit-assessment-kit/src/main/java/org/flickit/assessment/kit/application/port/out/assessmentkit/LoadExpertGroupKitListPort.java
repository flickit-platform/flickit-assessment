package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AssessmentKit;

import java.util.UUID;

public interface LoadExpertGroupKitListPort {

    PaginatedResponse<AssessmentKit> loadExpertGroupKits(long expertGroupId, UUID userId,
                                                         boolean includeUnpublishedKits, int page, int size);
}
