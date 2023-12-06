package org.flickit.assessment.kit.application.port.out.answeroptionimpact;

public interface CreateAnswerOptionImpactPort {

    Long persist(Param param);

    record Param(Long questionImpactId, Long optionId, Double value) {}
}
