package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.port.out.SendEmailPort;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMemberStatusPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE;
import static org.flickit.assessment.users.common.MessageKey.*;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class InviteExpertGroupMemberService implements InviteExpertGroupMemberUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final LoadExpertGroupMemberStatusPort loadExpertGroupMemberPort;
    private final InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final AppSpecProperties appSpecProperties;
    private final SendEmailPort sendEmailPort;

    @Override
    public void inviteMember(Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());
        Optional<Integer> memberStatus = loadExpertGroupMemberPort.getMemberStatus(param.getExpertGroupId(), param.getUserId());

        if (memberStatus.isPresent() && (memberStatus.get() == ExpertGroupAccessStatus.ACTIVE.ordinal()))
            throw new ResourceAlreadyExistsException(INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE);

        var inviteToken = UUID.randomUUID();
        var inviteDate = LocalDateTime.now();
        var inviteExpirationDate = inviteDate.plusDays(EXPIRY_DURATION.toDays());
        var email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

        inviteExpertGroupMemberPort.invite(toParam(param, inviteDate, inviteExpirationDate, inviteToken));
        sendInviteEmail(email, param.getExpertGroupId(), inviteToken);
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private InviteExpertGroupMemberPort.Param toParam(Param param, LocalDateTime inviteDate,
                                                      LocalDateTime inviteExpirationDate, UUID inviteToken) {
        return new InviteExpertGroupMemberPort.Param(
            param.getExpertGroupId(),
            param.getUserId(),
            inviteDate,
            inviteExpirationDate,
            inviteToken,
            ExpertGroupAccessStatus.PENDING,
            param.getCurrentUserId());
    }

    private void sendInviteEmail(String to, long expertGroupId, UUID inviteToken) {
        String subject = MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_SUBJECT);

        String inviteUrl = String.join("/", appSpecProperties.getHost(), appSpecProperties.getExpertGroupInviteUrlPath(),
            String.valueOf(expertGroupId), inviteToken.toString());

        String body = generateEmailBody(inviteUrl);
        log.debug("Sending 'invite to expertGroup [{}]' email to [{}]", expertGroupId, to);
        sendEmailPort.send(to, subject, body);
    }

    private String generateEmailBody(String inviteUrl) {
        if (appSpecProperties.getSupportEmail().isBlank())
            return MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY_WITHOUT_SUPPORT_EMAIL,
                inviteUrl,
                appSpecProperties.getName());
        return MessageBundle.message(INVITE_EXPERT_GROUP_MEMBER_MAIL_BODY,
            inviteUrl,
            appSpecProperties.getName(),
            appSpecProperties.getSupportEmail());
    }
}
