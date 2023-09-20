package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectValueMapper {

    public static SubjectValueJpaEntity mapToJpaEntity(Long subjectId){
        return new SubjectValueJpaEntity(
            null,
            null,
            subjectId,
            null
        );
    }
}
