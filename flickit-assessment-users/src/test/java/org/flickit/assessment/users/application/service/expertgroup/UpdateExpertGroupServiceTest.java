package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
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
class UpdateExpertGroupServiceTest {

    @InjectMocks
    UpdateExpertGroupService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    @DisplayName("If expert group not exists or the CurrentUser is not owner, service should throw AccessDenied")
    void testUpdateExpertGroup_invalidParameters_AccessDeniedException(){
        var expertGroupId = 1L;
        var currentUserId = UUID.randomUUID();
        UpdateExpertGroupUseCase.Param param = new UpdateExpertGroupUseCase.Param(expertGroupId, "title", "bio",
            "about", "https://www.google.com", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, ()-> service.updateExpertGroup(param));

    }

}
