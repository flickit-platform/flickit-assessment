package org.flickit.assessment.users.application.service.space;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteSpaceServiceTest {
    @InjectMocks
    DeleteSpaceService service;

    @Mock
    LoadSpaceOwnerPort loadSpaceOwnerPort;

//    @Mock
//    DeleteSpacePort deleteSpacePort;
//

    //
//    @Mock
//    CheckSpaceExistsPort checkSpaceExistsPort;
//
//    @Mock
//    CountSpaceAssessmentPort countSpaceAssessmentPort;
    @Test
    @DisplayName("Deleting a space should be accomplished by owner")
    void testDeleteSpase_currentUserIsNotOwner_resourceNotFound() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.deleteSpace(param));
        Assertions.assertThat(throwable).hasMessage(SPACE_ID_NOT_FOUND);
    }
}
