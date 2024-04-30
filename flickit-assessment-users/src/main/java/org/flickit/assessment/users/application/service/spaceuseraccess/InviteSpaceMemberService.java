package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.mail.SendFlickitInviteMailPort;
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

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_SPACE_MEMBER_SPACE_USER_DUPLICATE;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteSpaceMemberService implements InviteSpaceMemberUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadUserPort loadUserPort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;
    private final InviteSpaceMemberPort inviteSpaceMemberPort;
    private final SendFlickitInviteMailPort sendFlickitInviteMailPort;

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

            sendFlickitInviteMailPort.inviteToFlickit(param.getEmail());
        }
    }

    private InviteSpaceMemberPort.Param toParam(Long spaceId, String email, UUID createdBy,
                                                LocalDateTime creationTime, LocalDateTime expirationDate) {
        return new InviteSpaceMemberPort.Param(spaceId, email, createdBy, creationTime, expirationDate);
    }
}
