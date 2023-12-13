package org.flickit.assessment.kit.application.port.out.user;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;

public interface LoadUsersByKitPort {

    PaginatedResponse<GetKitUserListUseCase.KitUserListItem> load(Param param);

    record Param(Long kitId, int page, int size) {
    }
}
