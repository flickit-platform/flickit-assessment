package org.flickit.flickitassessmentcore.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetEvidenceListService implements GetEvidenceListUseCase {

    private final LoadEvidencesByQuestionPort loadEvidencesByQuestion;

    @Override
    public PaginatedResponse<EvidenceListItem> getEvidenceList(GetEvidenceListUseCase.Param param) {
        return loadEvidencesByQuestion.loadEvidencesByQuestionId(
            param.getQuestionId(),
            param.getPage(),
            param.getSize()
        );
    }
}
