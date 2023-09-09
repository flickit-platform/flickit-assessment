package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;


import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.Subject;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;

import java.util.ArrayList;

public class SubjectValueMapper {

    public static SubjectValueJpaEntity mapToJpaEntity(Long subjectId){
        return new SubjectValueJpaEntity(
            null,
            null,
            subjectId,
            null
        );
    }

    public static SubjectValue mapToDomainModel(SubjectValueJpaEntity entity) {
        return new SubjectValue(
            entity.getId(),
            new Subject(entity.getSubjectId()),
            new ArrayList<>(),
            new MaturityLevel(entity.getMaturityLevelId(), 0, new ArrayList<>()));
    }
}
