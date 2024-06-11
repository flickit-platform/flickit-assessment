package org.flickit.assessment.core.adapter.out.persistence.kit.subject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectMapper {

    public static Subject mapToDomainModel(SubjectJpaEntity entity, List<Attribute> attributes) {
        return new Subject(
            entity.getId(),
            entity.getTitle(),
            attributes
        );
    }

}
