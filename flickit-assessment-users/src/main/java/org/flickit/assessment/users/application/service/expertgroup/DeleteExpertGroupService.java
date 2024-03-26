package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.expertgroup.DeleteExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.CheckExpertGroupHavingKitPort;
import org.flickit.assessment.users.application.port.out.expertgroup.DeleteExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_ACCESS_DENIED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteExpertGroupService implements DeleteExpertGroupUseCase {

    private final DeleteExpertGroupPort deleteExpertGroupPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CheckExpertGroupHavingKitPort checkExpertGroupHavingKitPort;


    @Override
    public void deleteExpertGroup(Param param) {
        validateCurrentUser(param.getId(), param.getCurrentUserId());
        boolean havingKit = checkExpertGroupHavingKitPort.checkHavingKit(param.getId());

        if (havingKit)
            throw new AccessDeniedException(DELETE_EXPERT_GROUP_ACCESS_DENIED);

        deleteExpertGroupPort.deleteById(param.getId());
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
