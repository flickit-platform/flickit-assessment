package org.flickit.assessment.core.application.service.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
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
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightService implements GetAssessmentInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;

    @Override
    public Result getAssessmentInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId());
        if (assessmentResult.isEmpty())
            throw new ResourceNotFoundException(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND);
        validateAssessmentResultPort.validate(param.getAssessmentId());

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT);
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.get().getId());
        return assessmentInsight.map(insight -> getAssessorInsight(assessmentResult.get(), insight, editable))
            .orElseGet(() -> getDefaultInsight(assessmentResult.get(), editable));
    }

    private Result getAssessorInsight(AssessmentResult assessmentResult, AssessmentInsight assessmentInsight, boolean editable) {
        return new Result(null,
            new Result.AssessorInsight(assessmentInsight.getInsight(),
                assessmentInsight.getInsightTime(),
                assessmentResult.getLastCalculationTime().isBefore(assessmentInsight.getInsightTime())),
            editable);
    }

    private Result getDefaultInsight(AssessmentResult assessmentResult, boolean editable) {
        return new Result(new Result.DefaultInsight(createDefaultInsight(assessmentResult)),
            null,
            editable);
    }

    private String createDefaultInsight(AssessmentResult assessmentResult) {
        var progress = getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId());
        int questionsCount = progress.questionsCount();
        int answersCount = progress.answersCount();
        int confidenceValue = assessmentResult.getConfidenceValue() != null ? assessmentResult.getConfidenceValue().intValue() : 0;
        String maturityLevelTitle = assessmentResult.getMaturityLevel().getTitle();

        return (questionsCount == answersCount)
            ? MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED, maturityLevelTitle, questionsCount, confidenceValue)
            : MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE, maturityLevelTitle, answersCount, questionsCount, confidenceValue);
    }
}
