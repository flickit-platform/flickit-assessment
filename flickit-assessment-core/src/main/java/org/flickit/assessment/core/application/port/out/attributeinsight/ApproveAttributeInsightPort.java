package org.flickit.assessment.core.application.port.out.attributeinsight;

import java.util.UUID;

public interface ApproveAttributeInsightPort {

    void approve(UUID assessmentId, long attributeId);
}
