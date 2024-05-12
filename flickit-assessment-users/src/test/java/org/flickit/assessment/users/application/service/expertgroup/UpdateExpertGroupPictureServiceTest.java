package org.flickit.assessment.users.application.service.expertgroup;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupPictureUseCase.Param;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.UUID;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;



@ExtendWith(MockitoExtension.class)
class UpdateExpertGroupPictureServiceTest {

    @InjectMocks
    UpdateExpertGroupPictureService service;


    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    @DisplayName("Updating an expert group should be done on an existed expert group")
    void testUpdateExpertGroupPicture_expertGroupInvalid_resourceNotFound() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/images/image1.png"));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, ()->service.update(param), EXPERT_GROUP_ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Updating expert group should be done by the owner of the expert group")
    void testUpdateExpertGroupPicture_currentUserNotOwner_accessDenied() throws IOException {
        long expertGroupId = 0L;
        MockMultipartFile picture = new MockMultipartFile("images", "image1",
            "image/png", getResourceAsStream("/images/image1.png"));
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(expertGroupId, picture, currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId))
            .thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, ()->service.update(param), COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
