package org.flickit.assessment.core.application.port.out.adviceitem;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AdviceItem;

import java.util.UUID;

public interface LoadAdviceItemListPort {

    PaginatedResponse<AdviceItem> loadAll(UUID assessmentResultId, int page, int size);
}
