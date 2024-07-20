package org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.InviteAssessmentUserPort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaRepository;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee.AssessmentInviteeMapper.mapToJpaEntity;

@Component
@RequiredArgsConstructor
public class AssessmentInviteePersistenceJpaAdapter implements InviteAssessmentUserPort {

    private final AssessmentInviteeJpaRepository repository;

    @Override
    public void invite(Param param) {
        var invitation = repository.findByAssessmentIdAndEmail(param.assessmentId(), param.email());

        AssessmentInviteeJpaEntity entity;
        entity = invitation.map(assessmentInviteeJpaEntity -> mapToJpaEntity(invitation.get().getId(), param))
            .orElseGet(() -> mapToJpaEntity(null, param));

        repository.save(entity);
    }
}
