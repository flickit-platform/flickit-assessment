package org.flickit.assessment.kit.application.service.questionimpact;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.questionimpact.DeleteQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.DeleteQuestionImpactPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteQuestionImpactServiceTest {

    @InjectMocks
    private DeleteQuestionImpactService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteQuestionImpactPort deleteQuestionImpactPort;

    @Test
    void testDeleteQuestionImpact_kitVersionIdNoExists_ShouldThrowNotFoundException() {
        var param = createParam(DeleteQuestionImpactUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.delete(param));

        assertEquals(KIT_ID_NOT_FOUND, throwable.getMessage());

        verify(loadKitVersionPort).load(param.getKitVersionId());
        verifyNoInteractions(loadExpertGroupOwnerPort, deleteQuestionImpactPort);
    }

    @Test
    void testDeleteQuestionImpact_currentUserNotExpertGroupOwner_ShouldThrowNotAccessDeniedException() {
        var param = createParam(DeleteQuestionImpactUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadKitVersionPort).load(param.getKitVersionId());
        verify(loadExpertGroupOwnerPort).loadOwnerId(kitVersion.getKit().getExpertGroupId());
        verifyNoInteractions(deleteQuestionImpactPort);
    }

    @Test
    void testDeleteQuestionImpact_validParam_ShouldDeleteQuestionImpact() {
        var param = createParam(DeleteQuestionImpactUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        assertDoesNotThrow(() -> service.delete(param));

        verify(loadKitVersionPort).load(param.getKitVersionId());
        verify(loadExpertGroupOwnerPort).loadOwnerId(kitVersion.getKit().getExpertGroupId());
        verify(deleteQuestionImpactPort).deleteByIdAndKitVersionId(param.getQuestionImpactId(), param.getKitVersionId());
    }

    private DeleteQuestionImpactUseCase.Param createParam(Consumer<DeleteQuestionImpactUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteQuestionImpactUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteQuestionImpactUseCase.Param.builder()
            .questionImpactId(1L)
            .kitVersionId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
