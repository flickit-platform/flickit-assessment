package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
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
