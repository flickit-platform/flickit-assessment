package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAdviceAiNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_AI_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_AI_IS_DISABLED;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAdviceAiAiNarrationService implements CreateAdviceAiNarrationUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final CreateAdviceNarrationPort createAdviceNarrationPort;
    private final UpdateAdviceNarrationPort updateAdviceNarrationPort;
    private final AppAiProperties appAiProperties;
    private final OpenAiProperties openAiProperties;
    private final CallAiPromptPort callAiPromptPort;

    @Override
    public Result createAdviceAiNarration(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!appAiProperties.isEnabled())
            return new Result(MessageBundle.message(ADVICE_AI_IS_DISABLED));

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_AI_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());

        var prompt = openAiProperties.createAdviceAiNarrationPrompt(param.getAdviceListItems().toString(),
            param.getAttributeLevelTargets().toString());
        var aiNarration = callAiPromptPort.call(prompt);

        Runnable action = adviceNarration.isEmpty() ?
            () -> handleNewAdviceNarration(assessmentResult.getId(), aiNarration, param.getCurrentUserId()) :
            () -> handleExistingAdviceNarration(assessmentResult.getId(), aiNarration);
        action.run();

        return new Result(aiNarration);
    }

    private void handleExistingAdviceNarration(UUID assessmentResultId, String adviceAiNarration) {
        updateAdviceNarrationPort.updateAiNarration(toAdviceNarration(assessmentResultId, adviceAiNarration, null));
    }

    private void handleNewAdviceNarration(UUID assessmentResultId, String adviceAiNarration, UUID createdBy) {
        createAdviceNarrationPort.persist(toAdviceNarration(assessmentResultId, adviceAiNarration, createdBy));
    }

    AdviceNarration toAdviceNarration(UUID assessmentResultId, String aiNarration, UUID createdBy) {
        return new AdviceNarration(null, assessmentResultId,
            aiNarration,
            null,
            LocalDateTime.now(),
            null,
            createdBy);
    }
}
