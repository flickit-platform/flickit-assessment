package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAccessToKitService implements GrantUserAccessToKitUseCase {

    private final GrantUserAccessToKitPort grantUserAccessToKitPort;
    private final LoadExpertGroupIdPort loadExpertGroupIdPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public void grantUserAccessToKit(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getEmail());
        log.debug("User [{}] granted access to kit [{}]", param.getEmail(), param.getKitId());
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        Long expertGroupId = loadExpertGroupIdPort.loadExpertGroupId(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_KIT_ID_NOT_FOUND));
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(GRANT_USER_ACCESS_TO_KIT_EXPERT_GROUP_OWNER_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }
}
