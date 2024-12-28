package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AssessmentAccessCheckerService implements AssessmentAccessChecker {

    private final CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;
    private final AssessmentPermissionChecker assessmentPermissionChecker;

    @Override
    public boolean isAuthorized(UUID assessmentId, UUID userId, AssessmentPermission permission) {
        if (!checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(assessmentId, userId)) // todo: ruin the SRP rule!
            return false;
        return assessmentPermissionChecker.isAuthorized(assessmentId, userId, permission);
    }
}
