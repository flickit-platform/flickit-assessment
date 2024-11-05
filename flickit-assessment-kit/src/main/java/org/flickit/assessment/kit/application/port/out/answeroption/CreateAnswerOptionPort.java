package org.flickit.assessment.kit.application.port.out.answeroption;

import java.util.UUID;

public interface CreateAnswerOptionPort {

    long persist(Param param);

    record Param(
        String title,
        Integer index,
        Long answerRangeId,
        double value,
        Long kitVersionId,
        UUID createdBy) {}
}
