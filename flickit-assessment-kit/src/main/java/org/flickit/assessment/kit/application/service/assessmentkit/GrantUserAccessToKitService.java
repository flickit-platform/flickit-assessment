package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitOwnerPort;
import org.flickit.assessment.kit.application.port.out.useraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        boolean isAccessUpdated = grantUserAccessToKitPort.grantUserAccessToKitByUserEmail(param.getKitId(), param.getUserEmail());
        if (!isAccessUpdated) {
//            TODO
            throw new RuntimeException();
        }
    }

    private void validateCurrentUser(Param param) {
        User user = loadKitOwnerPort.loadKitOwnerById(param.getKitId());
        if (!user.getId().equals(param.getCurrentUser())) {
//            TODO
            throw new RuntimeException();
        }
    }
}
