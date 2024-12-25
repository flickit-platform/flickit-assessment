package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.ApproveAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.attributeinsight.ApproveAttributeInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAttributeInsightService implements ApproveAttributeInsightUseCase {

    private final ApproveAttributeInsightPort approveAttributeInsightPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public void approveAttributeInsight(Param param) {
        UUID assessmentId = param.getAssessmentId();
        if (!assessmentAccessChecker.isAuthorized(assessmentId,
            param.getCurrentUserId(),
            AssessmentPermission.APPROVE_ATTRIBUTE_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        approveAttributeInsightPort.approveAttributeInsight(assessmentId, param.getAttributeId());
    }
}
