package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_ATTRIBUTES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentAttributesService implements GetAssessmentAttributesUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result getAssessmentAttributes(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return null;
    }
}
