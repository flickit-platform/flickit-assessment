package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceService implements GetEvidenceUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public Result getEvidence(Param param) {
        var portResult = loadEvidencePort.loadEvidenceWithDetails(param.getId());

        if (!checkUserAssessmentAccessPort.hasAccess(portResult.assessmentId() ,param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return mapToResult(portResult);
    }

    Result mapToResult(LoadEvidencePort.Result portResult) {
        var confidenceLevel = ConfidenceLevel.valueOfById(portResult.answer().confidenceLevel());
        return new Result(portResult.id(),
            portResult.description(),
            new Result.Questionnaire(portResult.questionnaire().id(), portResult.questionnaire().title()),
            new Result.Question(portResult.question().id(), portResult.question().title(), portResult.question().index()),
            new Result.Answer(new Result.AnswerOption(portResult.answer().answerOption().id(), portResult.answer().answerOption().title(), portResult.answer().answerOption().index()),
                new Result.ConfidenceLevel(confidenceLevel.getId(), confidenceLevel.getTitle()), portResult.answer().isNotApplicable()),
            portResult.createdBy(),
            portResult.creationTime(),
            portResult.lastModificationTime());
    }
}
