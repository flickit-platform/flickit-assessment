package org.flickit.assessment.kit.application.port.out.answerrange;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CreateAnswerRangePort {

    long persist(Param param);

    Map<String, Long> persistAll(List<Param> params);

    record Param(long kitVersionId, String title, String code, boolean reusable, UUID createdBy) {}
}
