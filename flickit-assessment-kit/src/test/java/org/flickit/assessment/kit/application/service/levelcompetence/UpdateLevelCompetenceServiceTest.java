package org.flickit.assessment.kit.application.service.levelcompetence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.levelcompetence.UpdateLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLevelCompetenceServiceTest {

    @InjectMocks
    private UpdateLevelCompetenceService service;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateLevelCompetencePort updateLevelCompetencePort;

    @Mock
    private DeleteLevelCompetencePort deleteLevelCompetencePort;


    @Test
    void testUpdateLevelCompetence_kitIdInvalid_ShouldReturnResourceNotFoundException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateLevelCompetence(param));
        assertEquals(KIT_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testUpdateLevelCompetence_CurrentUserIsNotExpertGroupOwner_ShouldReturnAccessDeniedException() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId);
        var assessmentKit = mock(AssessmentKit.class);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateLevelCompetence(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateLevelCompetence_ValidParamsAndValueIsNotZero_SuccessfulUpdateLevelCompetence() {
        var currentUserId = UUID.randomUUID();
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, 3, currentUserId);
        var assessmentKit = mock(AssessmentKit.class);
        ArgumentCaptor<UpdateLevelCompetencePort.Param> updatePortParam = ArgumentCaptor.forClass(UpdateLevelCompetencePort.Param.class);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(currentUserId);
        doNothing().when(updateLevelCompetencePort).updateValue(any());

        assertDoesNotThrow(() -> service.updateLevelCompetence(param));
        verify(updateLevelCompetencePort).updateValue(updatePortParam.capture());
        assertEquals(param.getLevelCompetenceId(), updatePortParam.getValue().id());
        assertEquals(currentUserId, updatePortParam.getValue().lastModifiedBy());
        verifyNoInteractions(deleteLevelCompetencePort);
    }

    @Test
    void testUpdateLevelCompetence_ValidParamsAndValueIsZero_SuccessfulDeleteLevelCompetence() {
        var currentUserId = UUID.randomUUID();
        var value = 0;
        var assessmentKit = AssessmentKitMother.simpleKit();
        var param = new UpdateLevelCompetenceUseCase.Param(1L, 2L, value, currentUserId);
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(currentUserId);
        doNothing().when(deleteLevelCompetencePort).deleteByIdAndKitVersionId(1L, assessmentKit.getKitVersionId());

        assertDoesNotThrow(() -> service.updateLevelCompetence(param));
        verify(deleteLevelCompetencePort).deleteByIdAndKitVersionId(1L, assessmentKit.getKitVersionId());
        verifyNoInteractions(updateLevelCompetencePort);
    }
}
