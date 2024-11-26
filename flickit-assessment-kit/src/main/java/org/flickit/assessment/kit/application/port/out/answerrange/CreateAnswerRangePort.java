package org.flickit.assessment.kit.application.port.out.answerrange;

import java.util.UUID;

public interface CreateAnswerRangePort {

    long persist(Param param);

    record Param(long kitVersionId, String title, String code, boolean reusable, UUID createdBy) {}
}
