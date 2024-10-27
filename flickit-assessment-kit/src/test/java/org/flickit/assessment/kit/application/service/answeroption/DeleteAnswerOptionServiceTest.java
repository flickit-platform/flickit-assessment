package org.flickit.assessment.kit.application.service.answeroption;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.answeroption.DeleteAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.DeleteAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAnswerOptionServiceTest {

    @InjectMocks
    private DeleteAnswerOptionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteAnswerOptionPort deleteAnswerOptionPort;

    @Test
    void deleteAnswerOptionServiceTest_kitVersionIdNotExists_shouldThrowResourceNotFoundException() {
        var param = createParam(DeleteAnswerOptionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));

        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());

        verify(loadKitVersionPort, times(1)).load(param.getKitVersionId());
        verifyNoMoreInteractions(loadExpertGroupOwnerPort, deleteAnswerOptionPort);
    }

    @Test
    void deleteAnswerOptionServiceTest_currentUserIsNotExpertGroupOwner_shouldThrowAccessDeniedException() {
        var param = createParam(DeleteAnswerOptionUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadKitVersionPort, times(1)).load(param.getKitVersionId());
        verify(loadExpertGroupOwnerPort, times(1)).loadOwnerId(kitVersion.getKit().getExpertGroupId());
        verifyNoInteractions(deleteAnswerOptionPort);
    }

    @Test
    void deleteAnswerOptionServiceTest_validParams_shouldDeleteAnswerOption() {
        var param = createParam(DeleteAnswerOptionUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        assertDoesNotThrow(() -> service.delete(param));

        verify(deleteAnswerOptionPort, times(1)).delete(param.getAnswerOptionId(), param.getKitVersionId());
    }

    private DeleteAnswerOptionUseCase.Param createParam(Consumer<DeleteAnswerOptionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteAnswerOptionUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAnswerOptionUseCase.Param.builder()
            .answerOptionId(1L)
            .kitVersionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
