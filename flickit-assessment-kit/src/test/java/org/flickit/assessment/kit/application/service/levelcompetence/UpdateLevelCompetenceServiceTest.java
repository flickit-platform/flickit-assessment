package org.flickit.assessment.kit.application.service.levelcompetence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.levelcompetence.UpdateLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.UpdateLevelCompetencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLevelCompetenceServiceTest {

    @InjectMocks
    private UpdateLevelCompetenceService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateLevelCompetencePort updateLevelCompetencePort;

    @Mock
    private DeleteLevelCompetencePort deleteLevelCompetencePort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateLevelCompetence_CurrentUserIsNotExpertGroupOwner_ShouldReturnAccessDeniedException() {
        var param = createParam(UpdateLevelCompetenceUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateLevelCompetence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateLevelCompetence_ValidParamsAndValueIsNotZero_SuccessfulUpdateLevelCompetence() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateLevelCompetencePort).updateById(anyLong(), anyLong(), anyInt(), any());

        service.updateLevelCompetence(param);

        verify(updateLevelCompetencePort).updateById(param.getLevelCompetenceId(), param.getKitVersionId(), param.getValue(), param.getCurrentUserId());
        verifyNoInteractions(deleteLevelCompetencePort);
    }

    @Test
    void testUpdateLevelCompetence_ValidParamsAndValueIsZero_SuccessfulDeleteLevelCompetence() {
        var param = createParam(b -> b.value(0).currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(deleteLevelCompetencePort).deleteByIdAndKitVersionId(anyLong(), anyLong());

        service.updateLevelCompetence(param);

        verify(deleteLevelCompetencePort).deleteByIdAndKitVersionId(param.getLevelCompetenceId(), param.getKitVersionId());
        verifyNoInteractions(updateLevelCompetencePort);
    }

    private UpdateLevelCompetenceUseCase.Param createParam(Consumer<UpdateLevelCompetenceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateLevelCompetenceUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateLevelCompetenceUseCase.Param.builder()
                .levelCompetenceId(123L)
                .kitVersionId(1L)
                .value(55)
                .currentUserId(UUID.randomUUID());
    }
}
