package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;

import java.util.UUID;

public interface LoadEvidencesByQuestionAndAssessmentPort {

    PaginatedResponse<EvidenceListItem> loadEvidencesByQuestionIdAndAssessmentId(Long questionId, UUID assessmentId, int page, int size);

}
