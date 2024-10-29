package org.flickit.assessment.kit.application.port.out.answeroption;

import java.util.UUID;

public interface CreateAnswerOptionPort {

    Long persist(Param param);

    record Param(
        String title,
        Integer index,
        Long questionId,
        Long answerRangeId,
        Long kitVersionId,
        UUID createdBy) {}
}
