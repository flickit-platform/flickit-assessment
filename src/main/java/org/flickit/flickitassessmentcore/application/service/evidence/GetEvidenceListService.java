package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionAndAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceListService implements GetEvidenceListUseCase {

    private final LoadEvidencesByQuestionAndAssessmentPort loadEvidencesPort;
    private final CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Override
    public PaginatedResponse<EvidenceListItem> getEvidenceList(GetEvidenceListUseCase.Param param) {
        if (!checkAssessmentExistencePort.existsById(param.getAssessmentId()))
            throw new ResourceNotFoundException(GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_FOUND);
        return loadEvidencesPort.loadEvidencesByQuestionIdAndAssessmentId(
            param.getQuestionId(),
            param.getAssessmentId(),
            param.getPage(),
            param.getSize()
        );
    }
}
