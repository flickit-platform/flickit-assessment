package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;

import java.time.LocalDateTime;
import java.util.HashSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitMapper {

    public static AssessmentKitJpaEntity toJpaEntity(CreateAssessmentKitPort.Param param, Long kitVersionId) {
        return new AssessmentKitJpaEntity(
            null,
            param.code(),
            param.title(),
            param.summary(),
            param.about(),
            param.published(),
            param.isPrivate(),
            param.expertGroupId(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.createdBy(),
            param.createdBy(),
            new HashSet<>(),
            LocalDateTime.now(),
            kitVersionId
        );
    }

    public static AssessmentKitJpaEntity toJpaEntity(AssessmentKitJpaEntity entity, EditKitInfoUseCase.Param param) {
        return new AssessmentKitJpaEntity(
            entity.getId(),
            entity.getCode(),
            param.getTitle() != null ? param.getTitle() : entity.getTitle(),
            param.getSummary() != null ? param.getSummary() : entity.getSummary(),
            param.getAbout() != null ? param.getAbout() : entity.getAbout(),
            param.getIsActive() != null ? param.getIsActive() : entity.getPublished(),
            param.getIsPrivate() != null ? param.getIsPrivate() : entity.getIsPrivate(),
            entity.getExpertGroupId(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            param.getCurrentUserId(),
            entity.getAccessGrantedUsers(),
            entity.getLastMajorModificationTime(),
            entity.getKitVersionId()
        );
    }
}
