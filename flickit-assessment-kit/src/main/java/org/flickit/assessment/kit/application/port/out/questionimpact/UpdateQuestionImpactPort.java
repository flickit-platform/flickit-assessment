package org.flickit.assessment.kit.application.port.out.questionimpact;

public interface UpdateQuestionImpactPort {

    void update(Param param);

    record Param(Long id, int weight, Long questionId) {
    }
}
