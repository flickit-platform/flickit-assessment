package org.flickit.assessment.core.adapter.out.persistence.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.DeleteAssessmentUserInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentsUserInvitationsPort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssessmentInviteePersistenceJpaAdapter implements
    LoadAssessmentsUserInvitationsPort,
    DeleteAssessmentUserInvitationPort {

    private final AssessmentInviteeJpaRepository repository;

    @Override
    public List<AssessmentInvitee> loadInvitations(String email) {
        var invitations = repository.findAllByEmail(email);

        return invitations
            .stream()
            .map(AssessmentInviteeMapper::mapToDomain)
            .toList();
    }

    @Override
    public void deleteAssessmentUserInvitationsByEmail(String email) {
        repository.deleteByEmail(email);
    }
}
