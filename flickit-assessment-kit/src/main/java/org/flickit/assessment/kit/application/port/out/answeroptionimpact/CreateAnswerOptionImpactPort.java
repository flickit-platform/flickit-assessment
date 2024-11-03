package org.flickit.assessment.kit.application.port.out.answeroptionimpact;

import java.util.UUID;

public interface CreateAnswerOptionImpactPort {

    Long persist(Param param);

    record Param(Long questionImpactId, Long optionId, Double value, Long kitVersionId, UUID createdBy) {}
}
