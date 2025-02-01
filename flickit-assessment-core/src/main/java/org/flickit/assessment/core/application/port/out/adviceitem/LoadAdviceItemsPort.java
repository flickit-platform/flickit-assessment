package org.flickit.assessment.core.application.port.out.adviceitem;

import org.flickit.assessment.core.application.domain.AdviceItem;

import java.util.List;
import java.util.UUID;

public interface LoadAdviceItemsPort {

    List<AdviceItem> loadAll(UUID assessmentResultId);
}
