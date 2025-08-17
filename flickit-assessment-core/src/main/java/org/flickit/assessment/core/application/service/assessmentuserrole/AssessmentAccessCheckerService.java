package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssessmentAccessCheckerService implements AssessmentAccessChecker {

    private final LoadAssessmentPort loadAssessmentPort;
    private final AssessmentPermissionChecker assessmentPermissionChecker;

    @Override
    public boolean isAuthorized(UUID assessmentId, UUID userId, AssessmentPermission permission) {
        if (!loadAssessmentPort.isAssessmentSpaceMember(assessmentId, userId))
            return false;
        return assessmentPermissionChecker.isAuthorized(assessmentId, userId, permission);
    }
}
