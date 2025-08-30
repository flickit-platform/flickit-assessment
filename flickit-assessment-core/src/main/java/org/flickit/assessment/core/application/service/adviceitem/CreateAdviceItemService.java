package org.flickit.assessment.core.application.service.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.adviceitem.CreateAdviceItemUseCase;
import org.flickit.assessment.core.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ADVICE_ITEM;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAdviceItemService implements CreateAdviceItemUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CreateAdviceItemPort createAdviceItemPort;

    @Override
    public Result createAdviceItem(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND));

        return new Result(createAdviceItemPort.persist(toCreateParam(param), assessmentResult.getId()));
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, MANAGE_ADVICE_ITEM))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private CreateAdviceItemPort.Param toCreateParam(Param param) {
        return new CreateAdviceItemPort.Param(
            param.getTitle(),
            param.getDescription(),
            CostLevel.valueOf(param.getCost()),
            PriorityLevel.valueOf(param.getPriority()),
            ImpactLevel.valueOf(param.getImpact()),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
