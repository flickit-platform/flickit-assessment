package org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.CreateAssessmentInviteePort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentInviteePersistenceJpaAdapter implements CreateAssessmentInviteePort {

    private final AssessmentInviteeJpaRepository repository;

    @Override
    public void persist(Param param) {
        var entity = new AssessmentInviteeJpaEntity(null,
            param.assessmentId(),
            param.email(),
            param.roleId(),
            param.expirationDate(),
            param.creationTime(),
            param.createdBy());
        repository.save(entity);
    }
}
