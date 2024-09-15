package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.GetAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.advice.common.ErrorMessageKey.GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;

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
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));
        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);
        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());
        if (adviceNarration.isEmpty()) {
            if (!appAiProperties.isEnabled()) {
                var aiNarration = new Result.AdviceNarration(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), null);
                return new Result(aiNarration, null, false);
            }
            return new Result(null, null, editable);
        }

        var narration = adviceNarration.get();
        if (narration.getAssessorNarration() == null) {
            var aiNarration = new Result.AdviceNarration(narration.getAiNarration(), narration.getAiNarrationTime());
            return new Result(aiNarration, null, editable);
        }

        var assessorNarration = new Result.AdviceNarration(narration.getAssessorNarration(), narration.getAssessorNarrationTime());
        return new Result(null, assessorNarration, editable);
    }
}
