package org.flickit.assessment.core.application.service.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectInsightService implements GetSubjectInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectInsightPort loadSubjectInsightPort;

    @Override
    public Result getSubjectInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        validateAssessmentResultPort.validate(param.getAssessmentId());

        var editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT);

        var subjectInsight = loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId());

        if (subjectInsight.isEmpty())
            return new Result(null, null, false, false);

        var insight = subjectInsight.get();
        return (insight.getInsightBy() == null)
            ? getDefaultInsight(assessmentResult, insight, editable)
            : getAssessorInsight(assessmentResult, insight, editable);
    }

    private Result getDefaultInsight(AssessmentResult assessmentResult, SubjectInsight insight, boolean editable) {
        return new Result(new Result.Insight(insight.getInsight(),
            insight.getInsightTime(),
            isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime())),
            null,
            editable,
            insight.isApproved());
    }

    private Result getAssessorInsight(AssessmentResult assessmentResult, SubjectInsight insight, boolean editable) {
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
