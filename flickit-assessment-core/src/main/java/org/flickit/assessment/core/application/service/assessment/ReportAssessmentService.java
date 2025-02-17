package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.ReportAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportAssessmentService implements ReportAssessmentUseCase {

    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentReportInfoPort loadReportInfoPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public Result reportAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var assessmentReport = loadReportInfoPort.load(param.getAssessmentId());

        log.debug("AssessmentReport returned for assessmentId=[{}].", param.getAssessmentId());

        var spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(assessmentReport.assessment().space().id());

        boolean manageable = isManageable(param.getAssessmentId(), param.getCurrentUserId(), spaceOwnerId);
        boolean exportable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT);
        var permissions = new Permissions(manageable, exportable);

        return new Result(assessmentReport.assessment(), assessmentReport.subjects(), permissions);
    }

    private boolean isManageable(UUID assessmentId, UUID currentUserId, UUID spaceOwnerId) {
        if (Objects.equals(currentUserId, spaceOwnerId))
            return true;

        var userRole = loadUserRoleForAssessmentPort.load(assessmentId, currentUserId);
        return userRole.map(role -> role.equals(MANAGER)).orElse(false);
    }
}
