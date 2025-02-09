package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.GetAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if(!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);
        boolean aiEnabled = appAiProperties.isEnabled();

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());
        if (adviceNarration.isEmpty())
            return new Result(null, null, editable, aiEnabled);

        var narration = adviceNarration.get();

        if(narration.getAiNarration() == null && narration.getAssessorNarration() != null){
            var assessorNarration = new Result.AdviceNarration(narration.getAssessorNarration(), narration.getAssessorNarrationTime());
            return new Result(null, assessorNarration, editable, aiEnabled);
        }

        if(narration.getAssessorNarration() == null && narration.getAiNarration() != null){
            var aiNarration = new Result.AdviceNarration(narration.getAiNarration(), narration.getAiNarrationTime());
            return new Result(aiNarration, null, editable, aiEnabled);
        }

        if (narration.getAiNarration() != null && narration.getAssessorNarrationTime().isBefore(narration.getAiNarrationTime())) {
            var aiNarration = new Result.AdviceNarration(narration.getAiNarration(), narration.getAiNarrationTime());
            return new Result(aiNarration, null, editable, aiEnabled);
        }

        var assessorNarration = new Result.AdviceNarration(narration.getAssessorNarration(), narration.getAssessorNarrationTime());
        return new Result(null, assessorNarration, editable, aiEnabled);
    }
}
