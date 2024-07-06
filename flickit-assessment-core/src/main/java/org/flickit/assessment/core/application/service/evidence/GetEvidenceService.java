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
        var confidenceLevel = ConfidenceLevel.valueOfById(portResult.evidenceAnswer().confidenceLevel());
        return new Result(portResult.id(),
            portResult.description(),
            new Result.EvidenceQuestionnaire(portResult.evidenceQuestionnaire().id(), portResult.evidenceQuestionnaire().title()),
            new Result.EvidenceQuestion(portResult.evidenceQuestion().id(), portResult.evidenceQuestion().title(), portResult.evidenceQuestion().index()),
            new Result.EvidenceAnswer(new Result.EvidenceAnswerOption(portResult.evidenceAnswer().evidenceAnswerOption().id(), portResult.evidenceAnswer().evidenceAnswerOption().title(), portResult.evidenceAnswer().evidenceAnswerOption().index()),
                new Result.EvidenceConfidenceLevel(confidenceLevel.getId(), confidenceLevel.getTitle()), portResult.evidenceAnswer().isNotApplicable()),
            portResult.createdBy(),
            portResult.creationTime(),
            portResult.lastModificationTime());
    }
}
