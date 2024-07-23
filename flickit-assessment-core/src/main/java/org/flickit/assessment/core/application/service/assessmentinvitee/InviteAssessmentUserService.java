package org.flickit.assessment.core.application.service.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase;
import org.flickit.assessment.core.application.port.mail.SendFlickitInviteMailPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.CreateAssessmentInvitationPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitationPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteAssessmentUserService implements InviteAssessmentUserUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;
    private final CreateSpaceInvitationPort createSpaceInvitationPort;
    private final CreateAssessmentInvitationPort createAssessmentInvitationPort;
    private final SendFlickitInviteMailPort sendFlickitInviteMailPort;
    private final CreateAssessmentSpaceUserAccessPort createAssessmentSpaceUserAccessPort;
    private final GrantUserAssessmentRolePort grantUserAssessmentRolePort;
    private final CheckSpaceAccessPort checkSpaceAccessPort;

    @Override
    public void inviteUser(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_USER_ASSESSMENT_ROLE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId()).orElseThrow();

        var user = loadUserPort.loadByEmail(param.getEmail());
        var creationTime = LocalDateTime.now();
        var expirationTime = creationTime.plusDays(EXPIRY_DURATION.toDays());
        if (user.isEmpty()) {
            createSpaceInvitationPort.persist(toCreateSpaceInvitationParam(assessment.getSpace().getId(), param, expirationTime, creationTime));
            createAssessmentInvitationPort.persist(toCreateAssessmentInvitationParam(param, expirationTime, creationTime));
            sendFlickitInviteMailPort.inviteToFlickit(param.getEmail());
        } else {
            var userId = user.get().getId();

            if (!checkSpaceAccessPort.checkIsMember(assessment.getSpace().getId(), userId)) {
                var createAssessmentParam = new CreateAssessmentSpaceUserAccessPort.Param(
                    assessment.getId(), userId, param.getCurrentUserId(), creationTime);
                createAssessmentSpaceUserAccessPort.persist(createAssessmentParam);
            }
            grantUserAssessmentRolePort.persist(param.getAssessmentId(), userId, param.getRoleId());
        }
    }

    CreateSpaceInvitationPort.Param toCreateSpaceInvitationParam(long spaceId, Param param, LocalDateTime expirationTime, LocalDateTime creationTime) {
        return new CreateSpaceInvitationPort.Param(spaceId,
            param.getEmail(),
            expirationTime,
            creationTime,
            param.getCurrentUserId());
    }

    CreateAssessmentInvitationPort.Param toCreateAssessmentInvitationParam(Param param, LocalDateTime expirationTime, LocalDateTime creationTime) {
        return new CreateAssessmentInvitationPort.Param(param.getAssessmentId(),
            param.getEmail(),
            param.getRoleId(),
            expirationTime,
            creationTime,
            param.getCurrentUserId());
    }
}
