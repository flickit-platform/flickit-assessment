package org.flickit.assessment.advice.application.service.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAdviceService implements CreateAdviceUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final CreateAdviceHelper createAdviceHelper;

    @Override
    public Result createAdvice(Param param) {
        UUID assessmentId = param.getAssessmentId();

        validateUserAccess(assessmentId, param.getCurrentUserId());
        validateAssessmentResultPort.validate(assessmentId);

        List<AdviceListItem> adviceListItems = createAdviceHelper.createAdvice(assessmentId, param.getAttributeLevelTargets());
        return new Result(adviceListItems);
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
