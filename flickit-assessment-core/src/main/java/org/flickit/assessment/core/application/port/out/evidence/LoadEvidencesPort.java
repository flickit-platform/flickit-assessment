package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadEvidencesPort {

    PaginatedResponse<EvidenceListItem> loadNotDeletedEvidences(Long questionId, UUID assessmentId, int page, int size);

    record EvidenceListItem(UUID id,
                            String description,
                            String type,
                            LocalDateTime lastModificationTime,
                            Integer attachmentsCount,
                            User createdBy,
                            Boolean editable,
                            Boolean deletable) {}

    record User(UUID id,
                String displayName,
                String pictureLink) {
    }
}
