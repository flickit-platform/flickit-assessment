package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;

import java.util.UUID;

public interface LoadPublishedKitListPort {

    PaginatedResponse<Result> loadPublicKits(KitLanguage kitLanguage, int page, int size);

    PaginatedResponse<Result> loadPrivateKits(UUID userId, KitLanguage kitLanguage, int page, int size);

    record Result(AssessmentKit kit, ExpertGroup expertGroup) {
    }
}
