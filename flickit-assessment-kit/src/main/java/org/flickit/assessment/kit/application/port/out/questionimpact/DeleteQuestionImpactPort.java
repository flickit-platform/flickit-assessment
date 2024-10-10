package org.flickit.assessment.kit.application.port.out.questionimpact;

public interface DeleteQuestionImpactPort {

    void delete(Long questionImpactId, Long kitVersionId);
}
