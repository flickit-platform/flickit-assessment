package org.flickit.assessment.kit.application.port.out.assessmentkit;

import jakarta.annotation.Nullable;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.ExpertGroup;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface LoadPublishedKitListPort {

    PaginatedResponse<Result> loadPublicKits(@Nullable Collection<KitLanguage> kitLanguages, int page, int size);

    PaginatedResponse<Result> loadPrivateKits(UUID userId, @Nullable Collection<KitLanguage> kitLanguages, int page, int size);

    PaginatedResponse<Result> loadPrivateAndPublicKits(UUID currentUserId, @Nullable Set<KitLanguage> kitLanguages, int page, int size);

    record Result(AssessmentKit kit, ExpertGroup expertGroup) {
    }
}
