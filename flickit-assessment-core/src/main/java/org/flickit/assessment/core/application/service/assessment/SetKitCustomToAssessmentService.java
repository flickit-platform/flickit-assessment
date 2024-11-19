package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.SetKitCustomToAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_KIT_CUSTOM;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class SetKitCustomToAssessmentService implements SetKitCustomToAssessmentUseCase {

    private final UpdateAssessmentPort updateAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void setKitCustomToAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_KIT_CUSTOM))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        updateAssessmentPort.updateKitCustomId(param.getAssessmentId(), param.getKitCustomId());
    }
}
