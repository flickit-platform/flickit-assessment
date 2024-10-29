package org.flickit.assessment.kit.application.port.out.answeroption;

import java.util.UUID;

public interface CreateAnswerOptionPort {

    long persist(Param param);

    record Param(String title, int index, long questionId, double value, long kitVersionId, UUID createdBy) {}
}
