package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.port.in.advicenarration.GetAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.in.advicenarration.GetAdviceNarrationUseCase.Result.Issues;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.advice.common.ErrorMessageKey.GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAdviceNarrationService implements GetAdviceNarrationUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final AppAiProperties appAiProperties;

    @Override
    public Result getAdviceNarration(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);
        boolean aiEnabled = appAiProperties.isEnabled();

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());

        if (adviceNarration.isEmpty()) {
            var issues = new Issues(true, false, false);
            return new Result(null, null, issues, editable, aiEnabled);
        }

        var narration = adviceNarration.get();
        var issues = buildIssues(narration, assessmentResult.getLastCalculationTime());
        return (narration.getCreatedBy() == null)
            ? getAiNarration(narration, issues, editable, aiEnabled)
            : getAssessorNarration(narration, issues, editable, aiEnabled);
    }

    private Issues buildIssues(AdviceNarration narration, LocalDateTime lastCalculationTime) {
        boolean expired = narration.getCreatedBy() == null
            ? narration.getAiNarrationTime().isBefore(lastCalculationTime)
            : narration.getAssessorNarrationTime().isBefore(lastCalculationTime);

        return new Issues(false, !narration.isApproved(), expired);
    }

    private Result getAiNarration(AdviceNarration narration, Issues issues, boolean editable, boolean aiEnabled) {
        var aiNarration = new Result.AdviceNarration(narration.getAiNarration(), narration.getAiNarrationTime());
        return new Result(aiNarration, null, issues, editable, aiEnabled);
    }

    private Result getAssessorNarration(AdviceNarration narration, Issues issues, boolean editable, boolean aiEnabled) {
        var assessorNarration = new Result.AdviceNarration(narration.getAssessorNarration(), narration.getAssessorNarrationTime());
        return new Result(null, assessorNarration, issues, editable, aiEnabled);
    }
}
