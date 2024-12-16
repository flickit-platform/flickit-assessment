package org.flickit.assessment.advice.application.port.out.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadAdviceItemListPort {

    PaginatedResponse<AdviceItem> loadAll(UUID assessmentResultId, int page, int size);
}
