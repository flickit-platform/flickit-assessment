package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.DeleteUserAccessOnKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.LoadKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.useraccess.DeleteUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserAccessOnKitService implements DeleteUserAccessOnKitUseCase {

    private final LoadExpertGroupIdPort loadExpertGroupIdPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final LoadKitUserAccessPort loadKitUserAccessPort;
    private final DeleteUserAccessPort deleteUserAccessPort;

    @Override
    public void delete(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        loadKitUserAccessPort.load(param.getKitId(), param.getCurrentUserId()).orElseThrow(
            () -> new ResourceNotFoundException(DELETE_USER_ACCESS_KIT_USER_NOT_FOUND)
        );

        deleteUserAccessPort.delete(new DeleteUserAccessPort.Param(param.getKitId(), param.getEmail()));
        log.debug("User [{}] access to private kit [{}] is removed.", param.getCurrentUserId(), param.getCurrentUserId());
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        Long expertGroupId = loadExpertGroupIdPort.loadExpertGroupId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_USER_ACCESS_KIT_ID_NOT_FOUND));
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(DELETE_USER_ACCESS_EXPERT_GROUP_OWNER_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }
}
