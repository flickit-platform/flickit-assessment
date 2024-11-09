package org.flickit.assessment.core.test.fixture.application;


import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public class SubjectValueMother {

    public static SubjectValue withAttributeValuesAndWeight(List<AttributeValue> qaValues, int weight) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.withWeight(weight), qaValues);
    }

    public static SubjectValue withQAValuesAndSubjectWithQAs(List<AttributeValue> qaValues, List<Attribute> qas) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.withAttributes(qas), qaValues);
    }
}
