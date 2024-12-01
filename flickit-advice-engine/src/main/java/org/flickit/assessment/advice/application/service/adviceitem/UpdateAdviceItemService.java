package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.UpdateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.UPDATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAdviceItemService implements UpdateAdviceItemUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final UpdateAdviceItemPort updateAdviceItemPort;

    @Override
    public void updateAdviceItem(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        updateAdviceItemPort.updateAdviceItem(toAdviceItem(param, assessmentResult.getId()));
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private AdviceItem toAdviceItem(UpdateAdviceItemUseCase.Param param, UUID assessmentResultId) {
        return new AdviceItem(param.getAdviceItemId(),
            param.getTitle(),
            assessmentResultId,
            param.getDescription(),
            CostLevel.valueOf(param.getCost()),
            PriorityLevel.valueOf(param.getPriority()),
            ImpactLevel.valueOf(param.getImpact()),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.getCurrentUserId(),
            param.getCurrentUserId());
    }
}
