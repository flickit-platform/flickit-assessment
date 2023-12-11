package org.flickit.assessment.kit.application.port.out.user;

import org.flickit.assessment.kit.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;

public interface LoadUsersByKitPort {

    PaginatedResponse<GetUserListUseCase.UserListItem> load(Param param);

    record Param(Long kitId, int page, int size) {
    }
}
