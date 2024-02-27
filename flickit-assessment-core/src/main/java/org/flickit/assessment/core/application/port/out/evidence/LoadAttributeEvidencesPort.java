package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.AttributeEvidenceListItem;

import java.util.UUID;

public interface LoadAttributeEvidencesPort {

    PaginatedResponse<AttributeEvidenceListItem> loadAttributeEvidences(UUID assessmentId,
                                                                        Long attributeId,
                                                                        Integer type,
                                                                        int page,
                                                                        int size);
}
