package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.subject.SubjectMapper;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectValueMapper {

    public static SubjectValueJpaEntity mapToJpaEntity(Long subjectId) {
        return new SubjectValueJpaEntity(
            null,
            null,
            subjectId,
            null,
            null
        );
    }

    public static SubjectValue mapToDomainModel(SubjectValueJpaEntity entity, SubjectJpaEntity subjectEntity) {
        var subject = SubjectMapper.mapToDomainModel(subjectEntity, null);
        return new SubjectValue(
            entity.getId(),
            subject,
            new ArrayList<>()
        );
    }

    public static SubjectValue mapToDomainModel(SubjectValueJpaEntity entity,
                                                SubjectJpaEntity subjectEntity,
                                                List<AttributeJpaEntity> attributeEntities,
                                                MaturityLevelJpaEntity maturityLevelEntity) {
        var attributes = attributeEntities.stream().map(AttributeMapper::mapToDomainModel).toList();
        var subject = SubjectMapper.mapToDomainModel(subjectEntity, attributes);
        return new SubjectValue(
            entity.getId(),
            subject,
            new ArrayList<>(),
            MaturityLevelMapper.mapToDomainModel(maturityLevelEntity, null),
            entity.getConfidenceValue(),
            null
        );
    }
}
