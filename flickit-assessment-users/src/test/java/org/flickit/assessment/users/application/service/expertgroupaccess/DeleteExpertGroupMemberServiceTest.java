package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupMemberServiceTest {

    @InjectMocks
    private DeleteExpertGroupMemberService service;
    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    void inviteMember_expertGroupNotExist_fail() {
        UUID userId = UUID.randomUUID();
        UUID token = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId,userId,currentUserId, token);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.deleteMember(param));
    }

    @Test
    void inviteMember_expertGroupInviterNotOwner_fail() {
        UUID userId = UUID.randomUUID();
        UUID token = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId,userId,currentUserId, token);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(Optional.of(UUID.randomUUID()));

        Assertions.assertThrows(AccessDeniedException.class, () -> service.deleteMember(param));
    }
}
