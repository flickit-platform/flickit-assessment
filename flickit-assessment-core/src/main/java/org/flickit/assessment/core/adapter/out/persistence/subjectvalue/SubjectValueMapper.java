package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.subject.SubjectMapper;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueWithSubjectView;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.util.ArrayList;

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

    public static SubjectValue mapToDomainModel(SubjectValueWithSubjectView entity, SubjectJpaEntity subjectEntity, MaturityLevelJpaEntity maturity) {
        var subject = SubjectMapper.mapToDomainModel(subjectEntity, null);
        var subjectValue = new SubjectValue(
            entity.getSubjectValue().getId(),
            subject,
            new ArrayList<>()
        );
        var maturityLevel = MaturityLevelMapper.mapToDomainModel(maturity, null);
        subjectValue.setMaturityLevel(maturityLevel);
        return subjectValue;
    }
}
