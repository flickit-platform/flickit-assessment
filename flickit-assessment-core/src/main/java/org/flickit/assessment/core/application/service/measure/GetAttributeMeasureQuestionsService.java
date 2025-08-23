package org.flickit.assessment.core.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeMeasureQuestionsService implements GetAttributeMeasureQuestionsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result getAttributeMeasureQuestions(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        return null;
    }
}
