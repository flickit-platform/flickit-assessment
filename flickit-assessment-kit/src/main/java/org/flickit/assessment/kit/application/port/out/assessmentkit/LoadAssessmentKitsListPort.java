package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase.KitsListItem;

import java.util.UUID;

public interface LoadAssessmentKitsListPort {

    PaginatedResponse<KitsListItem> loadKitsList(Param param);

    record Param(Boolean isPrivate, int page, int size, UUID currentUserId) {}
}
