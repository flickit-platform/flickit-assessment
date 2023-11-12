package org.flickit.assessment.core.adapter.out.persistence.subject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.subject.SubjectJpaEntity;
import org.flickit.assessment.kit.domain.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectMapper {
    public static Subject mapToKitDomainModel(SubjectJpaEntity entity) {
        return new Subject(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getIndex(),
            0, //TODO
            null // TODO
        );
    }
}
