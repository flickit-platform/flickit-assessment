package org.flickit.assessment.kit.application.service.levelcompetence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.levelcompetence.UpdateLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLevelCompetenceServiceTest {

    @InjectMocks
    private UpdateLevelCompetenceService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Mock
    private UpdateLevelCompetencePort updateLevelCompetencePort;

    @Test
    void testUpdateLevelCompetence_kitIdInvalid_ShouldReturnResourceNotFoundException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId);

        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateLevelCompetence(param));
        assertEquals(KIT_ID_NOT_FOUND, throwable.getMessage());
        verifyNoInteractions(loadKitExpertGroupPort);
    }

    @Test
    void testUpdateLevelCompetence_ExpertGroupOfKitNotValid_ShouldReturnResourceNotFoundException() {
        var currentUserId = UUID.randomUUID();
        var kitVersionId = 123L;
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId);

        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateLevelCompetence(param));

        assertEquals(EXPERT_GROUP_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testUpdateLevelCompetence_CurrentUserIsNotExpertGroupOwner_ShouldReturnAccessDeniedException() {
        var currentUserId = UUID.randomUUID();
        var kitVersionId = 123L;
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId);
        var expertGroup = ExpertGroupMother.createExpertGroup();

        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateLevelCompetence(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateLevelCompetence_ValidParams_SuccessfulUpdate() {
        var currentUserId = UUID.randomUUID();
        var kitVersionId = 123L;
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId);
        var expertGroup = ExpertGroupMother.createExpertGroupWithCreatedBy(currentUserId);
        ArgumentCaptor<UpdateLevelCompetencePort.Param> updatePortParam = ArgumentCaptor.forClass(UpdateLevelCompetencePort.Param.class);

        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        doNothing().when(updateLevelCompetencePort).updateInfo(any());

        assertDoesNotThrow(() -> service.updateLevelCompetence(param));
        verify(updateLevelCompetencePort).updateInfo(updatePortParam.capture());
        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(kitVersionId, updatePortParam.getValue().kitVersionId());
        assertEquals(currentUserId, updatePortParam.getValue().lastModifiedBy());
    }
}
