package org.flickit.assessment.kit.application.service.levelcompetence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.levelcompetence.DeleteLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.levelcomptenece.DeleteLevelCompetencePort;
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
class DeleteLevelCompetenceServiceTest {

    @InjectMocks
    private DeleteLevelCompetenceService service;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private DeleteLevelCompetencePort deleteLevelCompetencePort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testDeleteLevelCompetence_WhenCurrentUserIsNotOwner_ThenThrowAccessDeniedException() {
        var param = createParam(DeleteLevelCompetenceUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteLevelCompetence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteLevelCompetencePort);
    }

   @Test
   void testDeleteLevelCompetence_WhenCurrentUserIsOwner_ThenDeleteLevelCompetence() {
       var param = createParam(b -> b.currentUserId(ownerId));

       when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
       when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
       doNothing().when(deleteLevelCompetencePort).delete(param.getLevelCompetenceId(), param.getKitVersionId());

       service.deleteLevelCompetence(param);

       verify(deleteLevelCompetencePort).delete(param.getLevelCompetenceId(), param.getKitVersionId());
   }

    private DeleteLevelCompetenceUseCase.Param createParam(Consumer<DeleteLevelCompetenceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteLevelCompetenceUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteLevelCompetenceUseCase.Param.builder()
            .kitVersionId(1L)
            .levelCompetenceId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
