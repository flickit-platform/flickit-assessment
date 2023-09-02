package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;

import java.util.List;

public interface LoadEvidencesByQuestionPort {

    PaginatedResponse<EvidenceListItem> loadEvidencesByQuestionId(Long questionId, int page, int size);

}
