package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;

import java.util.UUID;

public interface LoadEvidencesPort {

    PaginatedResponse<EvidenceListItem> loadNotDeletedEvidences(Long questionId, UUID assessmentId, int page, int size);

}
