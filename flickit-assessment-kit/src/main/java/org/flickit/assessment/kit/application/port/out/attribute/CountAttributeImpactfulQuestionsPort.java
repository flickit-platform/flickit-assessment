package org.flickit.assessment.kit.application.port.out.attribute;

public interface CountAttributeImpactfulQuestionsPort {

    int countQuestions(long attributeId, long kitVersionId);
}
