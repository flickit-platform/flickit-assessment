package org.flickit.assessment.users.application.service.spaceuseraccess;

import org.flickit.assessment.users.application.port.in.spaceuseraccess.GetSpaceMembersUseCase.Param;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSpaceMembersServiceTest {

    @InjectMocks
    GetSpaceMembersService service;

    @Mock
    CheckSpaceAccessPort checkSpaceAccessPort;

    @Test
    @DisplayName("Only members can see the Space members")
    void testGetSpaceMember_spaceAccessNotFound_emptyResponse(){
        long spaceId= 0L;
        UUID currentUserId = UUID.randomUUID();
        int size = 10;
        int page = 0;
        Param param = new Param(spaceId,currentUserId, size, page);

        when(checkSpaceAccessPort.checkIsMember(spaceId,currentUserId)).thenReturn(false);

        var result = service.getSpaceMembers(param);
        assertEquals(0, result.getItems().size(), "Items list should be empty");
        assertEquals(0, result.getTotal(), "'page' should be 0");
        assertEquals(0, result.getTotal(), "'size' should be 0");
        assertEquals(0, result.getTotal(), "'total' should be 0");
    }

}
