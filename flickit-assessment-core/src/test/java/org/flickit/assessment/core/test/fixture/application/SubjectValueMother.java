package org.flickit.assessment.core.test.fixture.application;


import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public class SubjectValueMother {

    public static SubjectValue withAttributeValues(List<AttributeValue> qaValues) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.withNoAttributes(), qaValues);
    }

    public static SubjectValue withQAValuesAndMaturityLevel(List<AttributeValue> qaValues, MaturityLevel maturityLevel) {
        SubjectValue subjectValue = new SubjectValue(UUID.randomUUID(), SubjectMother.withNoAttributes(), qaValues);
        subjectValue.setMaturityLevel(maturityLevel);
        return subjectValue;
    }

    public static SubjectValue withQAValuesAndMaturityLevelAndSubjectWithQAs(List<AttributeValue> qaValues, MaturityLevel maturityLevel, List<Attribute> qas) {
        SubjectValue subjectValue = new SubjectValue(UUID.randomUUID(), SubjectMother.withAttributes(qas), qaValues);
        subjectValue.setMaturityLevel(maturityLevel);
        return subjectValue;
    }

    public static SubjectValue withQAValuesAndSubjectWithQAs(List<AttributeValue> qaValues, List<Attribute> qas) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.withAttributes(qas), qaValues);
    }
}
