package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentService implements GetAssessmentUseCase {

    private final LoadUserPort loadUserPort;
    private final GetAssessmentPort getAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;
    private final AssessmentPermissionChecker assessmentPermissionChecker;

    @Override
    public Result getAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND));

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));

        var createdBy = loadUserPort.loadById(assessment.getCreatedBy())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ASSESSMENT_CREATED_BY_ID_NOT_FOUND));

        var userRole = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId());

        boolean viewable = assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT);

        return new Result(
            assessment.getId(),
            assessment.getTitle(),
            assessment.getSpace(),
            assessment.getAssessmentKit(),
            assessment.getCreationTime(),
            assessment.getLastModificationTime(),
            new User(createdBy.getId(), createdBy.getDisplayName()),
            viewable ? assessmentResult.getMaturityLevel() : null,
            assessmentResult.getIsCalculateValid(),
            userRole.map(role -> role.equals(MANAGER)).orElse(false),
            viewable);
    }
}
