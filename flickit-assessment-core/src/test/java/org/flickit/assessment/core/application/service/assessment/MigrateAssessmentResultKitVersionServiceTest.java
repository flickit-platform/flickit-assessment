package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.DeleteAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MIGRATE_KIT_VERSION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.*;
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
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private InvalidateAssessmentResultCalculatePort invalidateAssessmentResultCalculatePort;

    @Mock
    private UpdateAssessmentResultPort updateAssessmentResultPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private DeleteAnswerPort deleteAnswerPort;

    private final MigrateAssessmentResultKitVersionUseCase.Param param = createParam(MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder::build);

    @Test
    void testMigrateAssessmentResultKitVersionService_whenCurrentUserDoesNotHaveRequiredAccess_thenShouldThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MIGRATE_KIT_VERSION))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.migrateKitVersion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAssessmentResultPort,
            invalidateAssessmentResultCalculatePort,
            updateAssessmentResultPort,
            loadQuestionPort,
            deleteAnswerPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_whenAssessmentResultDoesNotExists_thenShouldThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MIGRATE_KIT_VERSION))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.migrateKitVersion(param));
        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(invalidateAssessmentResultCalculatePort,
            updateAssessmentResultPort,
            loadQuestionPort,
            deleteAnswerPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_whenActiveKitVersionDoesNotExists_thenShouldThrowValidationException() {
        var assessmentResult = validResultWithoutActiveVersion();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MIGRATE_KIT_VERSION))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));

        var throwable = assertThrows(ValidationException.class, () -> service.migrateKitVersion(param));
        assertEquals(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND, throwable.getMessageKey());

        verifyNoInteractions(invalidateAssessmentResultCalculatePort,
            updateAssessmentResultPort,
            loadQuestionPort,
            deleteAnswerPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_whenParametersAreValidAndThereAreNotAnyDeletedQuestions_thenSuccessfulUpdate() {
        var assessmentResult = validResult();
        var activeKitVersionId = assessmentResult.getAssessment().getAssessmentKit().getKitVersion();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MIGRATE_KIT_VERSION))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadIdsByKitVersionId(assessmentResult.getKitVersionId())).thenReturn(Set.of(1L, 2L, 3L, 4L, 5L));
        when(loadQuestionPort.loadIdsByKitVersionId(activeKitVersionId)).thenReturn(Set.of(1L, 2L, 3L, 4L, 5L));

        service.migrateKitVersion(param);

        verify(updateAssessmentResultPort, times(1)).updateKitVersionId(assessmentResult.getId(), activeKitVersionId);
        verify(invalidateAssessmentResultCalculatePort, times(1)).invalidateCalculate(assessmentResult.getId());
        verifyNoInteractions(deleteAnswerPort);
    }

    @Test
    void testMigrateAssessmentResultKitVersionService_whenParametersAreValidAndThereAreDeletedQuestions_thenSuccessfulUpdate() {
        var assessmentResult = resultWithDeprecatedKitVersion();
        var activeKitVersionId = assessmentResult.getAssessment().getAssessmentKit().getKitVersion();
        Set<Long> currentVersionQuestionIds = Set.of(1L, 2L, 3L, 4L, 5L);
        Set<Long> currentVersionQuestions = Set.of(1L, 3L, 5L, 6L);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MIGRATE_KIT_VERSION))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadIdsByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(currentVersionQuestionIds);
        when(loadQuestionPort.loadIdsByKitVersionId(activeKitVersionId))
            .thenReturn(currentVersionQuestions);

        ArgumentCaptor<Set<Long>> deleteArgumentCaptor = ArgumentCaptor.forClass(Set.class);

        service.migrateKitVersion(param);

        verify(deleteAnswerPort, times(1)).delete(eq(assessmentResult.getId()), deleteArgumentCaptor.capture());
        Set<Long> deletedQuestions = deleteArgumentCaptor.getValue();

        assertEquals(Set.of(2L, 4L), deletedQuestions);

        verify(updateAssessmentResultPort, times(1))
            .updateKitVersionId(assessmentResult.getId(), activeKitVersionId);
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
