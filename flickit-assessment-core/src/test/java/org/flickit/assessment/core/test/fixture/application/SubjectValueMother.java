package org.flickit.assessment.core.test.fixture.application;


import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.application.SubjectMother.subjectWithWeightAndAttributes;

public class SubjectValueMother {

    public static SubjectValue withAttributeValues(List<AttributeValue> attributeValues, int weight) {
        var attributes = attributeValues.stream().map(AttributeValue::getAttribute).toList();
        return new SubjectValue(UUID.randomUUID(), subjectWithWeightAndAttributes(weight, attributes), attributeValues);
    }

    public static SubjectValue createSubjectValue() {
        var attributeValues = List.of(
            AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, 123L),
            AttributeValueMother.hasFullScoreOnLevel23WithWeight(2, 124L));
        var attributes = attributeValues.stream()
            .map(AttributeValue::getAttribute)
            .toList();
        var subjectValue = new SubjectValue(UUID.randomUUID(), subjectWithWeightAndAttributes(4, attributes), attributeValues);
        subjectValue.setAttributeValues(attributeValues);
        subjectValue.setConfidenceValue(68D);
        subjectValue.setMaturityLevel(MaturityLevelMother.levelThree());

        return subjectValue;
    }
}
