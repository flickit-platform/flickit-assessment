package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceListService implements GetEvidenceListUseCase {

    private final LoadEvidencesPort loadEvidencesPort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public PaginatedResponse<EvidenceListItem> getEvidenceList(GetEvidenceListUseCase.Param param) {
        if (!checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        return loadEvidencesPort.loadNotDeletedEvidences(
            param.getQuestionId(),
            param.getAssessmentId(),
            param.getPage(),
            param.getSize()
        );
    }
}
