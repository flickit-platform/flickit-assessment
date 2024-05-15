package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceListService implements GetEvidenceListUseCase {

    private final LoadEvidencesPort loadEvidencesPort;

    @Override
    public PaginatedResponse<EvidenceListItem> getEvidenceList(GetEvidenceListUseCase.Param param) {
        return loadEvidencesPort.loadNotDeletedEvidences(
            param.getQuestionId(),
            param.getAssessmentId(),
            param.getPage(),
            param.getSize()
        );
    }
}
