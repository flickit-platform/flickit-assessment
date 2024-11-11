package org.flickit.assessment.kit.application.port.out.answeroptionimpact;

import java.util.List;
import java.util.UUID;

public interface CreateAnswerOptionImpactPort {

    Long persist(Param param);

    void persistAll(List<Param> params);

    record Param(Long questionImpactId, Long optionId, Double value, Long kitVersionId, UUID createdBy) {}
}
