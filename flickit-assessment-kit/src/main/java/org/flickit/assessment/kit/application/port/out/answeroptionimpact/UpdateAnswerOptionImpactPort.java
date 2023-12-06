package org.flickit.assessment.kit.application.port.out.answeroptionimpact;

public interface UpdateAnswerOptionImpactPort {

    void update(Param param);

    record Param(Long id, Double value) {
    }
}
