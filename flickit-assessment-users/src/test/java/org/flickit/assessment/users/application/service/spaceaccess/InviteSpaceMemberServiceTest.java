package org.flickit.assessment.users.application.service.spaceaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceaccess.InviteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.spaceaccess.CheckMemberSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceaccess.CheckSpaceExistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviteSpaceMemberServiceTest {

    @InjectMocks
    InviteSpaceMemberService service;
    @Mock
    private CheckSpaceExistencePort checkSpaceExistencePort;
    @Mock
    private CheckMemberSpaceAccessPort checkMemberSpaceAccessPort;
    @Test
    @DisplayName("Inviting member to an invalid space should cause a ValidationException")
    void inviteMember_spaceNotFound_validationException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId,email,currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(false);

        assertThrows(ValidationException.class, ()-> service.inviteMember(param));

        verify(checkSpaceExistencePort).existsById(spaceId);
    }

    @Test
    @DisplayName("Inviting member to a space by a non-member should cause AccessDeniedException")
    void inviteMember_inviterIsNotMember_AccessDeniedException() {
        long spaceId = 0;
        String email = "admin@asta.org";
        UUID currentUserId = UUID.randomUUID();
        var param = new InviteSpaceMemberUseCase.Param(spaceId,email,currentUserId);
        when(checkSpaceExistencePort.existsById(spaceId)).thenReturn(true);
        when(checkMemberSpaceAccessPort.checkAccess(currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, ()-> service.inviteMember(param));

        verify(checkSpaceExistencePort).existsById(spaceId);
    }
}
