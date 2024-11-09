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
}
