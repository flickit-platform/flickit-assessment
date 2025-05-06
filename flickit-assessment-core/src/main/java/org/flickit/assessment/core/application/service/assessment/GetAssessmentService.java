package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Result.Language;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelPort;
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
    private final LoadAssessmentPort loadAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;
    private final AssessmentPermissionChecker assessmentPermissionChecker;
    private final LoadMaturityLevelPort loadMaturityLevelPort;
    private final LoadAssessmentKitPort loadAssessmentKitPort;

    @Override
    public Result getAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessment = loadAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND));

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));

        var createdBy = loadUserPort.loadById(assessment.getCreatedBy())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ASSESSMENT_CREATED_BY_ID_NOT_FOUND));

        var userRole = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId());

        boolean viewable = assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT);

        var assessmentKit = loadAssessmentKitPort.loadAssessmentKit(assessment.getAssessmentKit().getId(), assessmentResult.getLanguage())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));

        MaturityLevel maturityLevel = null;
        if (viewable)
            maturityLevel = loadMaturityLevelPort.load(assessmentResult.getMaturityLevel().getId(), assessmentResult.getAssessment().getId());

        return new Result(
            assessment.getId(),
            assessment.getTitle(),
            assessment.getShortTitle(),
            assessment.getSpace(),
            assessment.getKitCustomId(),
            new Result.AssessmentKit(assessmentKit.getId(), assessmentKit.getTitle()),
            assessment.getCreationTime(),
            assessment.getLastModificationTime(),
            new User(createdBy.getId(), createdBy.getDisplayName(), null),
            maturityLevel,
            assessmentResult.getIsCalculateValid(),
            Language.of(assessmentResult.getLanguage()),
            userRole.map(role -> role.equals(MANAGER)).orElse(false),
            viewable);
    }
}
