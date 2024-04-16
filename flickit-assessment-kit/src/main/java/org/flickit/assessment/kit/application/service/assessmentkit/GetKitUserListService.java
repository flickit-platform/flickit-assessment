package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitUserListService implements GetKitUserListUseCase {

    private final LoadKitUsersPort loadKitUsersPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Override
    public PaginatedResponse<UserListItem> getKitUserList(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        return loadKitUsersPort.loadKitUsers(toParam(param.getKitId(), param.getPage(), param.getSize()));
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(kitId);
        if (!Objects.equals(expertGroup.getOwnerId(), currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private LoadKitUsersPort.Param toParam(Long kitId, int page, int size) {
        return new LoadKitUsersPort.Param(kitId, page, size);
    }
}
