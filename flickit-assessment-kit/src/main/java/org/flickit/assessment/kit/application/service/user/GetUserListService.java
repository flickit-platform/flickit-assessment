package org.flickit.assessment.kit.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;
import org.flickit.assessment.kit.application.port.out.user.LoadUsersByKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserListService implements GetUserListUseCase {

    private final LoadUsersByKitPort loadUsersByKitPort;

    @Override
    public PaginatedResponse<UserListItem> getUserList(Param param) {
        return loadUsersByKitPort.load(toParam(param.getKitId(), param.getPage(), param.getSize()));
    }

    private LoadUsersByKitPort.Param toParam(Long kitId, int page, int size) {
        return new LoadUsersByKitPort.Param(kitId, page, size);
    }
}
