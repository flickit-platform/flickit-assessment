package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MigrateAssessmentResultKitVersionServiceTest {

    @InjectMocks
    private MigrateAssessmentResultKitVersionService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private InvalidateAssessmentResultCalculatePort invalidateAssessmentResultCalculatePort;

    @Mock
    private UpdateAssessmentResultPort updateAssessmentResultPort;

    @Test
    void testMigrateAssessmentResultKitVersionService_AssessmentIdNotExist_ShouldThrowResourceNotFoundException() {
        var param = createParam(MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder::build);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.migrateKitVersion(param));

        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verify(getAssessmentPort, times(1))
            .getAssessmentById(param.getAssessmentId());

        verifyNoInteractions(assessmentAccessChecker, loadAssessmentResultPort, loadAssessmentResultPort,
            invalidateAssessmentResultCalculatePort, updateAssessmentResultPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_CurrentUserDoesNotHaveAccess_ShouldThrowAccessDeniedException() {
        var param = createParam(MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder::build);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(AssessmentMother.assessment()));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.migrateKitVersion(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker, times(1))
            .isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION);
        verifyNoInteractions(loadAssessmentResultPort, loadAssessmentResultPort,
            invalidateAssessmentResultCalculatePort, updateAssessmentResultPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_AssessmentResultNotExist_ShouldThrowResourceNotFoundException() {
        var param = createParam(MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder::build);
        var assessment = AssessmentMother.assessment();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION))
            .thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId()))
            .thenReturn(Optional.of(assessment));
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.migrateKitVersion(param));

        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND, throwable.getMessage());

        verify(assessmentAccessChecker, times(1))
            .isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION);
        verify(getAssessmentPort, times(1))
            .getAssessmentById(param.getAssessmentId());
        verify(loadAssessmentResultPort, times(1))
            .loadByAssessmentId(assessment.getId());

        verifyNoInteractions(invalidateAssessmentResultCalculatePort, updateAssessmentResultPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_ActiveKitVersionNotExist_ShouldThrowValidationException() {
        var param = createParam(MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder::build);
        var assessment = AssessmentMother.assessmentWithoutActiveVersion();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION))
            .thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId()))
            .thenReturn(Optional.of(assessment));
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(invalidateAssessmentResultCalculatePort).invalidateCalculate(assessmentResult.getId());
        doNothing().when(updateAssessmentResultPort).updateKitVersionId(assessmentResult.getId(), assessment.getAssessmentKit().getKitVersion());

        var throwable = assertThrows(ValidationException.class, () -> service.migrateKitVersion(param));

        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND, throwable.getMessageKey());

        verify(assessmentAccessChecker, times(1))
            .isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION);
        verify(getAssessmentPort, times(1))
            .getAssessmentById(param.getAssessmentId());
        verify(loadAssessmentResultPort, times(1))
            .loadByAssessmentId(assessment.getId());
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_ValidParameters_SuccessfulUpdate() {
        var param = createParam(MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder::build);
        var assessment = AssessmentMother.assessment();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION))
            .thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId()))
            .thenReturn(Optional.of(assessment));
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId()))
            .thenReturn(Optional.of(assessmentResult));

        service.migrateKitVersion(param);

        verify(assessmentAccessChecker, times(1))
            .isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MIGRATE_KIT_VERSION);
        verify(getAssessmentPort, times(1))
            .getAssessmentById(param.getAssessmentId());
        verify(loadAssessmentResultPort, times(1))
            .loadByAssessmentId(assessment.getId());
        verify(updateAssessmentResultPort, times(1))
            .updateKitVersionId(assessmentResult.getId(), assessment.getAssessmentKit().getKitVersion());
        verify(invalidateAssessmentResultCalculatePort, times(1))
            .invalidateCalculate(assessmentResult.getId());
    }

    private MigrateAssessmentResultKitVersionUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
