package org.flickit.assessment.core.application.service.insight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.insight.ApproveAllAssessmentInsightsUseCase;
import org.flickit.assessment.core.application.port.out.insight.assessment.ApproveAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.ApproveAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.ApproveSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class ApproveAllAssessmentInsightsService implements ApproveAllAssessmentInsightsUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ApproveAssessmentInsightPort approveAssessmentInsightPort;
    private final ApproveSubjectInsightPort approveSubjectInsightPort;
    private final ApproveAttributeInsightPort approveAttributeInsightPort;

    @Override
    public void approveAllAssessmentInsights(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        approveAssessmentInsightPort.approve(param.getAssessmentId(), LocalDateTime.now());
        approveSubjectInsightPort.approveAll(param.getAssessmentId(), LocalDateTime.now());
        approveAttributeInsightPort.approveAll(param.getAssessmentId(), LocalDateTime.now());
    }
}
