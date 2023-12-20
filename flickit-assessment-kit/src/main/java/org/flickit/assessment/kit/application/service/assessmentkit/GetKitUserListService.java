package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.crud.KitUserPaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_USER_LIST_EXPERT_GROUP_OWNER_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_USER_LIST_KIT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitUserListService implements GetKitUserListUseCase {

    private final LoadKitUsersPort loadKitUsersPort;
    private final LoadExpertGroupIdPort loadExpertGroupIdPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public KitUserPaginatedResponse getKitUserList(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        return loadKitUsersPort.load(toParam(param.getKitId(), param.getPage(), param.getSize()));
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        Long expertGroupId = loadExpertGroupIdPort.loadExpertGroupId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_USER_LIST_KIT_NOT_FOUND));
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_USER_LIST_EXPERT_GROUP_OWNER_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }

    private LoadKitUsersPort.Param toParam(Long kitId, int page, int size) {
        return new LoadKitUsersPort.Param(kitId, page, size);
    }
}
