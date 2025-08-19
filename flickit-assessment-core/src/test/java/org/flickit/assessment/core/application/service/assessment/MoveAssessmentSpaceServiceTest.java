package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.in.assessment.MoveAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CountAssessmentsPort;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MOVE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.flickit.assessment.core.test.fixture.application.SpaceMother.createBasicSpaceWithOwnerId;
import static org.flickit.assessment.core.test.fixture.application.SpaceMother.createPremiumSpaceWithOwnerId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoveAssessmentSpaceServiceTest {

    @InjectMocks
    private MoveAssessmentService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadSpacePort loadSpacePort;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private CountAssessmentsPort countAssessmentsPort;

    @Spy
    private AppSpecProperties appSpecProperties = appSpecProperties();

    private MoveAssessmentUseCase.Param param = createParam(MoveAssessmentUseCase.Param.ParamBuilder::build);
    private Space currentSpace = createBasicSpaceWithOwnerId(param.getCurrentUserId());
    private Space targetSpace = createBasicSpaceWithOwnerId(param.getCurrentUserId());

    @Test
    void testMoveAssessment_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.moveAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadSpacePort,
            appSpecProperties,
            countAssessmentsPort,
            updateAssessmentPort);
    }

    @Test
    void testMoveAssessment_whenCurrentSpaceDoesNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.moveAssessment(param));
        assertEquals(COMMON_SPACE_ID_NOT_FOUND, throwable.getMessage());

        verify(loadSpacePort, never()).loadById(anyLong());
        verifyNoInteractions(appSpecProperties,
            countAssessmentsPort,
            updateAssessmentPort);
    }

    @Test
    void testMoveAssessment_whenUserIsNotAssessmentSpaceOwner_thenThrowAccessDeniedException() {
        currentSpace = createBasicSpaceWithOwnerId(UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));

        var throwable = assertThrows(AccessDeniedException.class, () -> service.moveAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadSpacePort, never()).loadById(anyLong());
        verifyNoInteractions(appSpecProperties,
            countAssessmentsPort,
            updateAssessmentPort);
    }

    @Test
    void testMoveAssessment_whenCurrentSpaceAndTargetSpaceAreTheSame_thenThrowValidationException() {
        param = createParam(b -> b.targetSpaceId(currentSpace.getId()).currentUserId(currentSpace.getOwnerId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));

        var throwable = assertThrows(ValidationException.class, () -> service.moveAssessment(param));
        assertEquals(MOVE_ASSESSMENT_TARGET_SPACE_INVALID, throwable.getMessageKey());

        verify(loadSpacePort, never()).loadById(anyLong());
        verifyNoInteractions(appSpecProperties,
            countAssessmentsPort,
            updateAssessmentPort);
    }

    @Test
    void testMoveAssessment_whenTargetSpaceDoesNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpacePort.loadById(param.getTargetSpaceId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.moveAssessment(param));
        assertEquals(MOVE_ASSESSMENT_TARGET_SPACE_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(appSpecProperties,
            countAssessmentsPort,
            updateAssessmentPort);
    }

    @Test
    void testMoveAssessment_whenUserIsNotTargetSpaceOwner_thenThrowAccessDeniedException() {
        targetSpace = createBasicSpaceWithOwnerId(UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpacePort.loadById(param.getTargetSpaceId())).thenReturn(Optional.ofNullable(targetSpace));

        var throwable = assertThrows(AccessDeniedException.class, () -> service.moveAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(appSpecProperties,
            countAssessmentsPort,
            updateAssessmentPort);
    }

    @Test
    void testMoveAssessment_whenTargetSpaceIsBasicAndFull_thenThrowValidationException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpacePort.loadById(param.getTargetSpaceId())).thenReturn(Optional.of(targetSpace));
        when(countAssessmentsPort.countSpaceAssessments(targetSpace.getId()))
            .thenReturn(appSpecProperties().getSpace().getMaxBasicSpaceAssessments());

        var throwable = assertThrows(UpgradeRequiredException.class, () -> service.moveAssessment(param));
        assertEquals(MOVE_ASSESSMENT_TARGET_SPACE_ASSESSMENTS_MAX, throwable.getMessage());

        verify(appSpecProperties).getSpace();
        verifyNoInteractions(updateAssessmentPort);
    }

    @Test
    void testMoveAssessment_whenParametersAreValidAndTargetSpaceIsBasic_thenSuccessfulUpdate() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpacePort.loadById(param.getTargetSpaceId())).thenReturn(Optional.of(targetSpace));
        when(countAssessmentsPort.countSpaceAssessments(anyLong())).thenReturn(0);

        service.moveAssessment(param);

        verify(appSpecProperties).getSpace();
        verify(updateAssessmentPort).updateSpace(param.getAssessmentId(), param.getTargetSpaceId());
    }

    @Test
    void testMoveAssessment_whenParametersAreValidAndTargetSpaceIsPremium_thenSuccessfulUpdate() {
        targetSpace = createPremiumSpaceWithOwnerId(param.getCurrentUserId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            .thenReturn(true);
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(currentSpace));
        when(loadSpacePort.loadById(param.getTargetSpaceId())).thenReturn(Optional.of(targetSpace));

        service.moveAssessment(param);
        verify(updateAssessmentPort).updateSpace(param.getAssessmentId(), param.getTargetSpaceId());

        verifyNoInteractions(countAssessmentsPort,
            appSpecProperties);
    }

    AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        properties.setSpace(new AppSpecProperties.Space());
        properties.getSpace().setMaxBasicSpaceAssessments(2);
        return properties;
    }

    private MoveAssessmentUseCase.Param createParam(Consumer<MoveAssessmentUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private MoveAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return MoveAssessmentUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .targetSpaceId(0L)
            .currentUserId(UUID.randomUUID());
    }
}
