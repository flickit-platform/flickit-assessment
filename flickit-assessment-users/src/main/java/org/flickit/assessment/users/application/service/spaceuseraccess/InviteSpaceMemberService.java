package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_SPACE_USER_DUPLICATE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InviteSpaceMemberService implements InviteSpaceMemberUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadUserPort loadUserPort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;
    private final InviteSpaceMemberPort inviteSpaceMemberPort;
    private final AppSpecProperties appSpecProperties;
    private final SendEmailPort sendEmailPort;

    @Override
    public void inviteMember(Param param) {
        long spaceId = param.getSpaceId();
        var currentUserId = param.getCurrentUserId();

        if (!checkSpaceAccessPort.checkIsMember(spaceId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Optional<UUID> inviteeUserId = loadUserPort.loadUserIdByEmail(param.getEmail());
        if (inviteeUserId.isPresent()) {
            boolean inviteeHasAccess = checkSpaceAccessPort.checkIsMember(spaceId, inviteeUserId.get());
            if (inviteeHasAccess)
                throw new ResourceAlreadyExistsException(INVITE_SPACE_MEMBER_SPACE_USER_DUPLICATE);

            var access = new SpaceUserAccess(spaceId, inviteeUserId.get(), currentUserId, LocalDateTime.now());
            createSpaceUserAccessPort.persist(access);
        } else {
            var creationTime = LocalDateTime.now();
            var expirationDate = creationTime.plusDays(EXPIRY_DURATION.toDays());
            inviteSpaceMemberPort.invite(toParam(param.getSpaceId(), param.getEmail(), currentUserId, creationTime, expirationDate));

            sendInviteEmail(param.getEmail());
        }
    }

    private InviteSpaceMemberPort.Param toParam(Long spaceId, String email, UUID createdBy,
                                                LocalDateTime creationTime, LocalDateTime expirationDate) {
        return new InviteSpaceMemberPort.Param(spaceId, email, createdBy, creationTime, expirationDate);
    }

    private void sendInviteEmail(String sendTo) {
        String subject = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_SUBJECT, appSpecProperties.getName());
        String body = MessageBundle.message(INVITE_TO_REGISTER_EMAIL_BODY,
            appSpecProperties.getHost(),
            appSpecProperties.getName(),
            appSpecProperties.getSupportEmail());
        log.debug("Sending invite email to [{}]", sendTo);
        sendEmailPort.send(sendTo, subject, body);
    }
}
