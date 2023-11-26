package org.flickit.assessment.kit.adapter.out.persistence.subject;

import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.kit.application.domain.Subject;


public class SubjectMapper {

    public static Subject mapToDomainModel(SubjectJpaEntity entity) {
        return new Subject(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            null,
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }
}

