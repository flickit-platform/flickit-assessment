package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentInsightsIssuesUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightsIssuesService implements GetAssessmentInsightsIssuesUseCase {

    @Override
    public Result getInsightsIssues(Param param) {
        return null;
    }
}
