package org.flickit.assessment.core.test.fixture.application;


import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.application.SubjectMother.subjectWithAttributes;

public class SubjectValueMother {

    public static SubjectValue withAttributeValuesAndWeight(List<AttributeValue> attributeValues, int weight) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.subjectWithWeight(weight), attributeValues);
    }

    public static SubjectValue withQAValuesAndSubjectWithQAs(List<AttributeValue> qaValues, List<Attribute> qas) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.withAttributes(qas), qaValues);
    }
}
