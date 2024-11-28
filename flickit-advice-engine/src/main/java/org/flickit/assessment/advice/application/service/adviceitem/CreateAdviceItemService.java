package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.domain.adviceitem.CostType;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactType;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityType;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.advice.application.port.in.adviceitem.CreateAdviceItemUseCase;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAdviceItemService implements CreateAdviceItemUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final CreateAdviceItemPort createAdviceItemPort;

    @Override
    public Result createAdviceItem(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        return new Result(createAdviceItemPort.persist(toAdviceItem(param, assessmentResult.getId())));
    }

    private AdviceItem toAdviceItem(Param param, UUID assessmentResultId) {
        return new AdviceItem(null,
            param.getTitle(),
            assessmentResultId,
            param.getDescription(),
            param.getCost() != null ? CostType.valueOf(param.getCost()) : null,
            param.getPriority() != null ? PriorityType.valueOf(param.getPriority()) : null,
            param.getImpact() != null ? ImpactType.valueOf(param.getImpact()) : null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getCurrentUserId());
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
