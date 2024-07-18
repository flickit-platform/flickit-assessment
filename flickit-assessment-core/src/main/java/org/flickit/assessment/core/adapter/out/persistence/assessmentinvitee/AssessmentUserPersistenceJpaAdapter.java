package org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.InviteAssessmentUserPort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentUserPersistenceJpaAdapter implements InviteAssessmentUserPort {

    private final AssessmentInviteeJpaRepository repository;

    @Override
    public void invite(Param param) {
        var entity = new AssessmentInviteeJpaEntity(null,
            param.assessmentId(),
            param.email(),
            param.roleId(),
            param.creationTime(),
            param.expirationTime(),
            param.createdBy());

        if (repository.existsByAssessmentIdAndEmail(param.assessmentId(), param.email()))
            repository.update(param.assessmentId(), param.email(), param.roleId(), param.creationTime(), param.expirationTime(), param.createdBy());
        else
            repository.save(entity);
    }
}
