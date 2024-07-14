package org.flickit.assessment.core.application.service.assessmentuserinvitee;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteAssessmentUserService implements InviteAssessmentUserUseCase {

    @Override
    public void inviteUser(UUID assessmentId, UUID userId, Integer roleId) {
    }
}
