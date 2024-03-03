package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.util.ArrayList;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectValueMapper {

    public static SubjectValueJpaEntity mapToJpaEntity(Long subjectId, UUID subjectRefNum){
        return new SubjectValueJpaEntity(
            null,
            null,
            subjectId,
            subjectRefNum,
            null,
            null
        );
    }

    public static SubjectValue mapToDomainModel(SubjectValueJpaEntity entity, SubjectJpaEntity subjectEntity) {
        var subject = new Subject(subjectEntity.getId(), null);
        return new SubjectValue(
            entity.getId(),
            subject,
            new ArrayList<>()
        );
    }
}
