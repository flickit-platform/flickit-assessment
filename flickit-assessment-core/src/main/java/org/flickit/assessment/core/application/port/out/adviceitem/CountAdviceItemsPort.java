package org.flickit.assessment.core.application.port.out.adviceitem;

import java.util.UUID;

public interface CountAdviceItemsPort {

    int countAdviceItems(UUID assessmentResultId);
}
