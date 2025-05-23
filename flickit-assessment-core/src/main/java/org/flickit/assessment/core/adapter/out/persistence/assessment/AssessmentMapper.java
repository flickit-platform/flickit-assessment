package org.flickit.assessment.core.adapter.out.persistence.assessment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit.AssessmentKitMapper;
import org.flickit.assessment.core.adapter.out.persistence.space.SpaceMapper;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentKitSpaceJoinView;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentMapper {

    static AssessmentJpaEntity mapCreateParamToJpaEntity(CreateAssessmentPort.Param param) {
        return new AssessmentJpaEntity(
            null,
            param.code(),
            param.title(),
            param.shortTitle(),
            param.assessmentKitId(),
            param.spaceId(),
            null,
            param.mode().getId(),
            param.creationTime(),
            param.creationTime(),
            param.deletionTime(),
            param.deleted(),
            param.createdBy(),
            param.createdBy()
        );
    }

    public static Assessment mapToDomainModel(AssessmentKitSpaceJoinView view) {
        var kit = AssessmentKitMapper.mapToDomainModel(view.getKit());
        Space space = SpaceMapper.mapToDomain(view.getSpace());
        return mapToDomainModel(view.getAssessment(), kit, space);
    }

    public static Assessment mapToDomainModel(AssessmentJpaEntity assessment, AssessmentKit kit, Space space) {
        return new Assessment(
            assessment.getId(),
            assessment.getCode(),
            assessment.getTitle(),
            assessment.getShortTitle(),
            kit,
            space,
            assessment.getKitCustomId(),
            AssessmentMode.valueOfById(assessment.getMode()),
            assessment.getCreationTime(),
            assessment.getLastModificationTime(),
            assessment.getDeletionTime(),
            assessment.isDeleted(),
            assessment.getCreatedBy()
        );
    }
}
