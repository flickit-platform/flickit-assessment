package org.flickit.assessment.core.application.port.out.adviceitem;

import java.util.UUID;

public interface CountAdviceItemsPort {

    Result countAdviceItems(UUID assessmentResultId);

    record Result (long total) {
    }
}
