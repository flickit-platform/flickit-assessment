package org.flickit.assessment.core.application.service.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightService implements GetAssessmentInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;

    @Override
    public Result getAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT);
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());

        if (assessmentInsight.isEmpty())
            return new Result(null, null, editable, false);

        var insight = assessmentInsight.get();
        return (insight.getInsightBy() == null)
            ? getDefaultInsight(assessmentResult, insight, editable)
            : getAssessorInsight(assessmentResult, insight, editable);
    }

    private Result getDefaultInsight(AssessmentResult assessmentResult, AssessmentInsight insight, boolean editable) {
        return new Result(new Result.Insight(insight.getInsight(),
            insight.getInsightTime(),
            isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime())),
            null,
            editable,
            insight.isApproved());
    }

    private Result getAssessorInsight(AssessmentResult assessmentResult, AssessmentInsight insight, boolean editable) {
        return new Result(null,
            new Result.Insight(insight.getInsight(),
                insight.getInsightTime(),
                isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime())),
            editable,
            insight.isApproved());
    }

    private boolean isValid(LocalDateTime lastCalculationTime, LocalDateTime insightLastModificationTime) {
        return lastCalculationTime.isBefore(insightLastModificationTime);
    }
}
