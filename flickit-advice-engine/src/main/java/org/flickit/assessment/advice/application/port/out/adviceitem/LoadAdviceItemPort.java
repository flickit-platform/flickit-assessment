package org.flickit.assessment.advice.application.port.out.adviceitem;

import org.flickit.assessment.advice.application.domain.AdviceItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadAdviceItemPort {

    Optional<AdviceItem> load(UUID id);

    List<AdviceItem> loadAll(UUID assessmentResultId);
}
