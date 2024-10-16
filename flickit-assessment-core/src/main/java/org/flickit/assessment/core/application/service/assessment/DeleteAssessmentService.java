package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.DeleteAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.DeleteAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAssessmentService implements DeleteAssessmentUseCase {

    private final DeleteAssessmentPort deleteAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void deleteAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), DELETE_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        long deletionTime = System.currentTimeMillis();
        deleteAssessmentPort.deleteById(param.getId(), deletionTime);
    }
}
