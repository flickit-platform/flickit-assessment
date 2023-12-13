package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceListService implements GetEvidenceListUseCase {

    private final LoadEvidencesPort loadEvidencesPort;
    private final CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Override
    public PaginatedResponse<EvidenceListItem> getEvidenceList(GetEvidenceListUseCase.Param param) {
        if (!checkAssessmentExistencePort.existsById(param.getAssessmentId()))
            throw new ResourceNotFoundException(GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_FOUND);
        return loadEvidencesPort.loadNotDeletedEvidences(
            param.getQuestionId(),
            param.getAssessmentId(),
            param.getPage(),
            param.getSize()
        );
    }
}
