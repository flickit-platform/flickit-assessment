package org.flickit.assessment.core.application.service.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.LOAD_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.LOAD_ASSESSMENT_INSIGHT_DEFAULT_INSIGHT_TEXT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightService implements GetAssessmentInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;

    @Override
    public Result getAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId());
        if (assessmentResult.isEmpty())
            throw new ResourceNotFoundException(LOAD_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND);

        var hasCreatePermission = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT);
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.get().getId());
        return assessmentInsight.map(insight -> toResult(null, assessmentResult.get(), insight, hasCreatePermission))
            .orElse(toResult(createText(assessmentResult.get(), getAssessmentProgressPort.getProgress(param.getAssessmentId()).questionsCount()),
                null, null, hasCreatePermission));
    }

    private Result toResult(String defaultInsight, AssessmentResult assessmentResult, AssessmentInsight assessmentInsight, boolean hasCreatePermission) {
        return new Result(new Result.DefaultInsight(defaultInsight),
            assessmentInsight != null ? new Result.AssessorInsight(assessmentInsight.getInsight(),
                assessmentInsight.getInsightTime(),
                assessmentResult.getIsCalculateValid()) : null,
            hasCreatePermission);
    }

    private String createText(AssessmentResult assessmentResult, Integer questionsCount) {
        return MessageBundle.message(LOAD_ASSESSMENT_INSIGHT_DEFAULT_INSIGHT_TEXT,
            assessmentResult.getMaturityLevel().getTitle(),
            questionsCount + (questionsCount == 1 ? " question" : " questions"),
            assessmentResult.getConfidenceValue());
    }
}
