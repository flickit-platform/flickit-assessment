package org.flickit.assessment.core.adapter.out.persistence.assessmentinvite;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.out.assessmentinvite.CreateAssessmentInvitePort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentInviteMapper {

    public static AssessmentInvite mapToDomainModel(AssessmentInviteeJpaEntity entity) {
        return new AssessmentInvite(entity.getId(),
            entity.getAssessmentId(),
            entity.getEmail(),
            AssessmentUserRole.valueOfById(entity.getRoleId()),
            entity.getExpirationTime(),
            entity.getCreationTime(),
            entity.getCreatedBy());
    }

    public static AssessmentInviteeJpaEntity mapToJpaEntity(UUID id, CreateAssessmentInvitePort.Param param) {
        return new AssessmentInviteeJpaEntity(
            id,
            param.assessmentId(),
            param.email(),
            param.roleId(),
            param.expirationTime(),
            param.creationTime(),
            param.createdBy());
    }
}
