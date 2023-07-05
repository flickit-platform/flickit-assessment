package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

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
