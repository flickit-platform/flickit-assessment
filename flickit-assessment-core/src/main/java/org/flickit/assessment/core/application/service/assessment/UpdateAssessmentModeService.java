package org.flickit.assessment.core.application.service.assessment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentModeUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_ASSESSMENT_MODE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAssessmentModeService implements UpdateAssessmentModeUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateAssessmentPort updateAssessmentPort;

    @Override
    public void updateAssessmentMode(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), UPDATE_ASSESSMENT_MODE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateAssessmentPort.updateMode(param.getAssessmentId(), AssessmentMode.valueOf(param.getMode()));
    }
}
