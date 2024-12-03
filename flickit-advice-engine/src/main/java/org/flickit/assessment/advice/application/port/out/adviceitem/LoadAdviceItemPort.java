package org.flickit.assessment.advice.application.port.out.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;

import java.util.Optional;
import java.util.UUID;

public interface LoadAdviceItemPort {

    Optional<AdviceItem> load(UUID id);
}
