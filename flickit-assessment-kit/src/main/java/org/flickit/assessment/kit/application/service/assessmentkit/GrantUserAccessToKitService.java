package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerIdPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAccessToKitService implements GrantUserAccessToKitUseCase {

    private final GrantUserAccessToKitPort grantUserAccessToKitPort;
    private final LoadExpertGroupOwnerIdPort loadExpertGroupOwnerIdPort;

    @Override
    public void grantUserAccessToKit(Param param) {
        validateCurrentUser(param);
        grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getUserEmail());
        log.debug("User [{}] granted access to kit [{}]", param.getUserEmail(), param.getKitId());
    }

    private void validateCurrentUser(Param param) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerIdPort.loadByKitId(param.getKitId());
        if (!Objects.equals(expertGroupOwnerId, param.getCurrentUserId())) {
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        }
    }
}
