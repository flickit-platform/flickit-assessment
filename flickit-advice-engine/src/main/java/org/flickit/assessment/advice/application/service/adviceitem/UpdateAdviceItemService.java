package org.flickit.assessment.advice.application.service.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.UpdateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.UPDATE_ADVICE_ITEM_ADVICE_ITEM_NOT_FOUND;
import static org.flickit.assessment.advice.common.ErrorMessageKey.UPDATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ADVICE_ITEM;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAdviceItemService implements UpdateAdviceItemUseCase {

    private final LoadAdviceItemPort loadAdviceItemPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateAdviceItemPort updateAdviceItemPort;

    @Override
    public void updateAdviceItem(Param param) {
        var adviceItem = loadAdviceItemPort.loadAdviceItem(param.getAdviceItemId())
                .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ADVICE_ITEM_ADVICE_ITEM_NOT_FOUND));
        var assessmentResult = loadAssessmentResultPort.loadById(adviceItem.getAssessmentResultId())
                .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND));

        validateUserAccess(assessmentResult.getAssessmentId(), param.getCurrentUserId());

        updateAdviceItemPort.updateAdviceItem(toParam(param, assessmentResult.getId()));
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, MANAGE_ADVICE_ITEM))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private UpdateAdviceItemPort.Param toParam(Param param, UUID assessmentResultId) {
        return new UpdateAdviceItemPort.Param(param.getAdviceItemId(),
            param.getTitle(),
            assessmentResultId,
            param.getDescription(),
            CostLevel.valueOf(param.getCost()),
            PriorityLevel.valueOf(param.getPriority()),
            ImpactLevel.valueOf(param.getImpact()),
            LocalDateTime.now(),
            param.getCurrentUserId());
    }
}
