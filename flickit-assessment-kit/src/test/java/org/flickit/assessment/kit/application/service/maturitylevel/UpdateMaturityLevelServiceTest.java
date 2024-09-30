package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMaturityLevelServiceTest {

    @InjectMocks
    UpdateMaturityLevelService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    UpdateMaturityLevelPort updateMaturityLevelPort;

    @Test
    void testUpdateMaturityLevelService_KitIdNotFound_AccessDeniedException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateMaturityLevel(param));

        assertEquals(KIT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadExpertGroupOwnerPort, updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelService_UserIsNotExpertGroupOwner_AccessDeniedException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);
        var assessmentKit = mock(AssessmentKit.class);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(param.getKitId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateMaturityLevel(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelService_ValidParameters_SuccessfulUpdate() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateMaturityLevelUseCase.Param(1L, 0L, "title", 1, "description", 2, currentUserId);
        var assessmentKit = AssessmentKitMother.simpleKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(currentUserId);
        doNothing().when(updateMaturityLevelPort).update(any(), any(), any(), any());

        assertDoesNotThrow(() -> service.updateMaturityLevel(param));
    }
}
