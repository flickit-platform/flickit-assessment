package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceService implements GetEvidenceUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final LoadUserPort loadUserPort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public Result getEvidence(Param param) {
        var portResult = loadEvidencePort.loadEvidenceWithDetails(param.getId());

        if (!checkUserAssessmentAccessPort.hasAccess(portResult.assessmentId() ,param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var user = loadUserPort.loadById(portResult.createdBy()).orElseThrow();
        return mapToResult(portResult, user.getDisplayName());
    }

    Result mapToResult(LoadEvidencePort.Result portResult, String createdBy) {
        return new Result(portResult.id(),
            portResult.description(),
            new Questionnaire(portResult.questionnaire().id(), portResult.questionnaire().title()),
            new Question(portResult.question().id(), portResult.question().title(), portResult.question().index()),
            createdBy,
            portResult.creationTime(),
            portResult.lastModificationTime());
    }
}
