package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.subject.DeleteSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.DeleteSubjectPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_SUBJECT_KIT_DELETION_UNSUPPORTED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createActiveKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSubjectServiceTest {

    @InjectMocks
    private DeleteSubjectService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteSubjectPort deleteSubjectPort;

    @Test
    void testDeleteSubjectService_kitVersionDoesNotExist_throwsResourceNotFoundException() {
        var param = createParam(DeleteSubjectUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.deleteSubject(param));
        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadExpertGroupOwnerPort, deleteSubjectPort);
    }

    @Test
    void testDeleteSubjectService_CurrentUserIsNotExpertGroupOwner_throwsAccessDeniedException() {
        var param = createParam(DeleteSubjectUseCase.Param.ParamBuilder::build);
        var kitVersion = createKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteSubject(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteSubjectPort);
    }

    @Test
    void testDeleteSubjectService_KitVersionStatusIsNotUpdating_throwsValidationException() {
        var param = createParam(DeleteSubjectUseCase.Param.ParamBuilder::build);
        var kitVersion = createActiveKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        var throwable = assertThrows(ValidationException.class, () -> service.deleteSubject(param));
        assertEquals(DELETE_SUBJECT_KIT_DELETION_UNSUPPORTED, throwable.getMessageKey());

        verify(loadKitVersionPort).load(param.getKitVersionId());
        verify(loadExpertGroupOwnerPort).loadOwnerId(kitVersion.getKit().getExpertGroupId());
        verifyNoInteractions(deleteSubjectPort);
    }

    @Test
    void testDeleteSubjectService_validParams_successfulDelete() {
        var param = createParam(DeleteSubjectUseCase.Param.ParamBuilder::build);
        var kitVersion = createKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());
        doNothing().when(deleteSubjectPort).delete(param.getSubjectId(), param.getKitVersionId());

        service.deleteSubject(param);

        verify(deleteSubjectPort).delete(param.getSubjectId(), param.getKitVersionId());
    }

    private DeleteSubjectUseCase.Param createParam(Consumer<DeleteSubjectUseCase.Param.ParamBuilder> consumer) {
        var paramBuilder = paramBuilder();
        consumer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteSubjectUseCase.Param.builder()
            .subjectId(123L)
            .kitVersionId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
