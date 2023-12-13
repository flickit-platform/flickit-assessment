package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.user.LoadUsersByKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitUserListService implements GetKitUserListUseCase {

    private final LoadUsersByKitPort loadUsersByKitPort;

    @Override
    public PaginatedResponse<KitUserListItem> getKitUserList(Param param) {
        return loadUsersByKitPort.load(toParam(param.getKitId(), param.getPage(), param.getSize()));
    }

    private LoadUsersByKitPort.Param toParam(Long kitId, int page, int size) {
        return new LoadUsersByKitPort.Param(kitId, page, size);
    }
}
