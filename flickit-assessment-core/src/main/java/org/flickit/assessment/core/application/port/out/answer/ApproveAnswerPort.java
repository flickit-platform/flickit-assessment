package org.flickit.assessment.core.application.port.out.answer;

import java.util.List;
import java.util.UUID;

public interface ApproveAnswerPort {

    void approve(UUID answerId, UUID approvedBy);

    void approveAll(List<UUID> answerIds, UUID approvedBy);
}
