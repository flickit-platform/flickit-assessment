package org.flickit.assessment.core.application.service.assessmentuserinvitee;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.InviteAssessmentUserPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.mail.SendFlickitInviteMailPort;
import org.flickit.assessment.core.application.port.out.space.InviteSpaceMemberPort;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteAssessmentUserService implements InviteAssessmentUserUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;
    private final InviteSpaceMemberPort inviteSpaceMemberPort;
    private final InviteAssessmentUserPort inviteAssessmentUserPort;
    private final SendFlickitInviteMailPort sendFlickitInviteMailPort;

    @Override
    public void inviteUser(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId()).orElseThrow();

        var userRole = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId());
        if (!Objects.equals(userRole, MANAGER))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var creationTime = LocalDateTime.now();
        var expirationDate = creationTime.plusDays(EXPIRY_DURATION.toDays());
        inviteSpaceMemberPort.invite(new InviteSpaceMemberPort.Param(assessment.getSpace().getId(),
            param.getEmail(), param.getCurrentUserId(), creationTime, expirationDate));

        inviteAssessmentUserPort.persist(toParam(param, creationTime, expirationDate));
        sendFlickitInviteMailPort.inviteToFlickit(param.getEmail());
    }

    InviteAssessmentUserPort.Param toParam(Param param, LocalDateTime creationTime, LocalDateTime expirationDate) {
        return new InviteAssessmentUserPort.Param(param.getAssessmentId(),
            param.getEmail(),
            param.getRoleId(),
            creationTime,
            expirationDate,
            param.getCurrentUserId());
    }
}
