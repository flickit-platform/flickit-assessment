package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentProgressUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_PROGRESS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentProgressService implements GetAssessmentProgressUseCase {

    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result getAssessmentProgress(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_PROGRESS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var result = getAssessmentProgressPort.getProgress(param.getAssessmentId());
        return new Result(result.id(), result.answersCount(), result.questionsCount());
    }
}
