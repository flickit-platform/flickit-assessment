package org.flickit.assessment.core.application.service.assessmentuserinvitee;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.core.application.domain.SpaceUserAccess;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.InviteAssessmentUserPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.mail.SendFlickitInviteMailPort;
import org.flickit.assessment.core.application.port.out.space.InviteSpaceMemberPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.SpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.core.application.domain.AssessmentUserRole.MANAGER;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INVITE_ASSESSMENT_USER_EMAIL_DUPLICATE;
import static org.flickit.assessment.core.common.ErrorMessageKey.INVITE_ASSESSMENT_USER_ROLE_DUPLICATE;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteAssessmentUserService implements InviteAssessmentUserUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;
    private final InviteSpaceMemberPort inviteSpaceMemberPort;
    private final InviteAssessmentUserPort inviteAssessmentUserPort;
    private final SendFlickitInviteMailPort sendFlickitInviteMailPort;
    private final SpaceUserAccessPort spaceUserAccessPort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;
    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Override
    public void inviteUser(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId()).orElseThrow();
        if (loadUserPort.loadByEmail(param.getEmail()) != null)
            throw new ResourceAlreadyExistsException(INVITE_ASSESSMENT_USER_EMAIL_DUPLICATE);

        var user = loadUserPort.loadByEmail(param.getEmail());
        var creationTime = LocalDateTime.now();
        var expirationDate = creationTime.plusDays(EXPIRY_DURATION.toDays());
        if (user == null) {
            inviteSpaceMemberPort.invite(toInviteSpaceParam(assessment.getSpace().getId(), param.getEmail(),
                param.getCurrentUserId(), creationTime, expirationDate));
            inviteAssessmentUserPort.invite(toParam(param, creationTime, expirationDate));
            sendFlickitInviteMailPort.inviteToFlickit(param.getEmail());
        } else {
            if (checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), user.getId()))
                throw new ResourceAlreadyExistsException(INVITE_ASSESSMENT_USER_ROLE_DUPLICATE);
            var userAccess = new SpaceUserAccess(assessment.getSpace().getId(), user.getId(), param.getCurrentUserId(), creationTime);
            spaceUserAccessPort.persist(userAccess);
            grantUserAssessmentRolePort.persist(param.getAssessmentId(), user.getId(), param.getRoleId());
        }
    }

    InviteSpaceMemberPort.Param toInviteSpaceParam(long spaceId, String email, UUID createdBy, LocalDateTime creationTime, LocalDateTime expirationDate) {
        return new InviteSpaceMemberPort.Param(spaceId, email, createdBy, creationTime, expirationDate);
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
