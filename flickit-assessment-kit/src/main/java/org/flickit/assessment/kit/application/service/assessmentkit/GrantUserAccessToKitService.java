package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitOwnerPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GRANT_USER_ACCESS_TO_KIT_CURRENT_USER_NOT_KIT_OWNER;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GrantUserAccessToKitService implements GrantUserAccessToKitUseCase {

    private final GrantUserAccessToKitPort grantUserAccessToKitPort;
    private final LoadAssessmentKitOwnerPort loadKitOwnerPort;

    @Override
    public void grantUserAccessToKit(Param param) {
        validateCurrentUser(param);
        grantUserAccessToKitPort.grantUserAccess(param.getKitId(), param.getUserEmail());
        log.debug("User [{}] granted access to kit [{}]", param.getUserEmail(), param.getKitId());
    }

    private void validateCurrentUser(Param param) {
        User user = loadKitOwnerPort.loadKitOwnerById(param.getKitId());
        if (!Objects.equals(user.getId(), param.getCurrentUserId())) {
            throw new AccessDeniedException(GRANT_USER_ACCESS_TO_KIT_CURRENT_USER_NOT_KIT_OWNER);
        }
    }
}
