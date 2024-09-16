package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAssessorAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAssessorAdviceNarrationService implements CreateAssessorAdviceNarrationUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final CreateAdviceNarrationPort createAdviceNarrationPort;
    private final UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Override
    public void createAssessorAdviceNarration(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());

        adviceNarration.ifPresentOrElse(
            narration -> handleExistingAdviceNarration(param, narration),
            ()-> handleNewAdviceNarration(assessmentResult.getId(), param));
    }

    private void handleExistingAdviceNarration(Param param, AdviceNarration adviceNarration) {
        AdviceNarration updated = new AdviceNarration(adviceNarration.getId(),
            adviceNarration.getAssessmentResultId(),
            adviceNarration.getAiNarration(),
            param.getAssessorNarration(),
            adviceNarration.getAiNarrationTime(),
            LocalDateTime.now(),
            param.getCurrentUserId());
        updateAdviceNarrationPort.updateAssessorNarration(updated);
    }

    private void handleNewAdviceNarration(UUID assessmentResultId, Param param) {
        AdviceNarration narration = new AdviceNarration(null,
            assessmentResultId,
            null,
            param.getAssessorNarration(),
            null,
            LocalDateTime.now(),
            param.getCurrentUserId());
        createAdviceNarrationPort.persist(narration);
    }
}
