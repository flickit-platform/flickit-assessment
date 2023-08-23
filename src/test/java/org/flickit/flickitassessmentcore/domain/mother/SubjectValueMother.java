package org.flickit.flickitassessmentcore.domain.mother;


import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.domain.SubjectValue;

import java.util.List;
import java.util.UUID;

public class SubjectValueMother {

    public static SubjectValue withQAValues(List<QualityAttributeValue> qaValues) {
        return new SubjectValue(UUID.randomUUID(), qaValues);
    }

}
