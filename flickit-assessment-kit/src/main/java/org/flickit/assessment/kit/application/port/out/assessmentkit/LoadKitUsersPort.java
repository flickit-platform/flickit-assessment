package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;

public interface LoadKitUsersPort {

    PaginatedResponse<GetKitUserListUseCase.UserListItem> loadKitUsers(Param param);

    record Param(Long kitId, int page, int size) {
    }
}
