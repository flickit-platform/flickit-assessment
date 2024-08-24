package org.flickit.assessment.core.application.service.assessmentinvite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentinvite.InviteAssessmentUserUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentinvite.CreateAssessmentInvitePort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.GrantUserAssessmentRolePort;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InviteAssessmentUserService implements InviteAssessmentUserUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final GetAssessmentPort getAssessmentPort;
    private final LoadUserPort loadUserPort;
    private final CreateSpaceInvitePort createSpaceInvitePort;
    private final CreateAssessmentInvitePort createAssessmentInvitePort;
    private final AppSpecProperties appSpecProperties;
    private final SendEmailPort sendEmailPort;
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
            createSpaceInvitePort.persist(toCreateSpaceInviteParam(assessment.getSpace().getId(), param, expirationTime, creationTime));
            createAssessmentInvitePort.persist(toCreateAssessmentInviteParam(param, expirationTime, creationTime));
            sendInviteEmail(param.getEmail());
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

    CreateSpaceInvitePort.Param toCreateSpaceInviteParam(long spaceId, Param param, LocalDateTime expirationTime, LocalDateTime creationTime) {
        return new CreateSpaceInvitePort.Param(spaceId,
            param.getEmail(),
            expirationTime,
            creationTime,
            param.getCurrentUserId());
    }

    CreateAssessmentInvitePort.Param toCreateAssessmentInviteParam(Param param, LocalDateTime expirationTime, LocalDateTime creationTime) {
        return new CreateAssessmentInvitePort.Param(param.getAssessmentId(),
            param.getEmail(),
            param.getRoleId(),
            expirationTime,
            creationTime,
            param.getCurrentUserId());
    }

    private void sendInviteEmail(String sendTo) {
        String subject = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, appSpecProperties.getName());
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY, appSpecProperties.getHost(), appSpecProperties.getName());
        log.debug("Sending invite email to [{}]", sendTo);
        sendEmailPort.send(sendTo, subject, body);
    }
}
