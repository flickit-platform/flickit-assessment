package org.flickit.assessment.core.test.fixture.application;


import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.QualityAttributeValue;
import org.flickit.assessment.core.application.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public class SubjectValueMother {

    public static SubjectValue withQAValues(List<QualityAttributeValue> qaValues) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.withNoAttributes(), qaValues);
    }

    public static SubjectValue withQAValuesAndMaturityLevel(List<QualityAttributeValue> qaValues, MaturityLevel maturityLevel) {
        SubjectValue subjectValue = new SubjectValue(UUID.randomUUID(), SubjectMother.withNoAttributes(), qaValues);
        subjectValue.setMaturityLevel(maturityLevel);
        return subjectValue;
    }

    public static SubjectValue withQAValuesAndMaturityLevelAndSubjectWithQAs(List<QualityAttributeValue> qaValues, MaturityLevel maturityLevel, List<QualityAttribute> qas) {
        SubjectValue subjectValue = new SubjectValue(UUID.randomUUID(), SubjectMother.withAttributes(qas), qaValues);
        subjectValue.setMaturityLevel(maturityLevel);
        return subjectValue;
    }

    public static SubjectValue withQAValuesAndSubjectWithQAs(List<QualityAttributeValue> qaValues, List<QualityAttribute> qas) {
        return new SubjectValue(UUID.randomUUID(), SubjectMother.withAttributes(qas), qaValues);
    }
}
