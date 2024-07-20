package org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentInviteeMapper {

    public static AssessmentInvitee mapToDomainModel(AssessmentInviteeJpaEntity entity) {
        return new AssessmentInvitee(entity.getId(),
            entity.getEmail(),
            AssessmentUserRole.valueOfById(entity.getRoleId()),
            entity.getExpirationTime(),
            entity.getCreationTime(),
            entity.getCreatedBy());
    }
}
