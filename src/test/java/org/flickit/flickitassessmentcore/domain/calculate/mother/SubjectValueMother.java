package org.flickit.flickitassessmentcore.domain.calculate.mother;


import org.flickit.flickitassessmentcore.domain.calculate.SubjectValue;

import java.util.UUID;

public class SubjectValueMother {

    public static SubjectValue.SubjectValueBuilder builder() {
        return SubjectValue.builder()
            .id(UUID.randomUUID());
    }

}
