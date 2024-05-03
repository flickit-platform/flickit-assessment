package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAccessToKitService implements GrantUserAccessToKitUseCase {

    private final GrantUserAccessToKitPort grantUserAccessToKitPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Override
    public void grantUserAccessToKit(Param param) {
        validateCurrentUser(param.getKitId(), param.getCurrentUserId());
        grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getUserId());
        log.debug("User [{}] granted access to kit [{}]", param.getUserId(), param.getKitId());
    }

    private void validateCurrentUser(Long kitId, UUID currentUserId) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(kitId);
        if (!Objects.equals(expertGroup.getOwnerId(), currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
