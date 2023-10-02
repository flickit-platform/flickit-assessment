package org.flickit.flickitassessmentcore.application.domain.mother;


import org.flickit.flickitassessmentcore.application.domain.*;

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

    public static SubjectValue withQAValuesAndMaturityLevelAndSubject(List<QualityAttributeValue> qaValues, MaturityLevel maturityLevel, Subject subject) {
        SubjectValue subjectValue = new SubjectValue(UUID.randomUUID(), subject, qaValues);
        subjectValue.setMaturityLevel(maturityLevel);
        return subjectValue;
    }

    public static SubjectValue withQAValuesAndMaturityLevelAndSubjectWithQAs(List<QualityAttributeValue> qaValues, MaturityLevel maturityLevel, List<QualityAttribute> qas) {
        SubjectValue subjectValue = new SubjectValue(UUID.randomUUID(), SubjectMother.withAttributes(qas), qaValues);
        subjectValue.setMaturityLevel(maturityLevel);
        return subjectValue;
    }
}
