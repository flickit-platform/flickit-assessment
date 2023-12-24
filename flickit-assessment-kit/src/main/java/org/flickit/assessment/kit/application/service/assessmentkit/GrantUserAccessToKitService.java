package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAccessToKitService implements GrantUserAccessToKitUseCase {

    private final GrantUserAccessToKitPort grantUserAccessToKitPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public void grantUserAccessToKit(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        boolean isNew = grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getUserId());
        if (isNew) {
            log.debug("User [{}] granted access to kit [{}]", param.getUserId(), param.getKitId());
        }
        else {
            log.debug("User [{}] already has access to kit [{}]", param.getUserId(), param.getKitId());
            throw new ResourceAlreadyExistException(GRANT_USER_ACCESS_TO_KIT_USER_ID_DUPLICATE);
        }
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        Long expertGroupId = loadKitExpertGroupPort.loadKitExpertGroupId(kitId);
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId)) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }
}
