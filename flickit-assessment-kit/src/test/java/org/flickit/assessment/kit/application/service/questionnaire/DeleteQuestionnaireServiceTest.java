package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionnaire.DeleteQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.DeleteQuestionnairePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createActiveKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteQuestionnaireServiceTest {

    @InjectMocks
    private DeleteQuestionnaireService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteQuestionnairePort deleteQuestionnairePort;

    UUID ownerId = UUID.randomUUID();

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testDeleteQuestionnaire_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(DeleteQuestionnaireUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteQuestionnaire(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(deleteQuestionnairePort);
    }

    @Test
    void testDeleteQuestionnaire_WhenCurrentUserIsExpertGroupOwnerAndKitVersionStatusIsNotUpdating_ThenThrowAccessDeniedException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        KitVersion kitVersion = createActiveKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteQuestionnaire(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(deleteQuestionnairePort);
    }

    @Test
    void testDeleteQuestionnaire_WhenCurrentUserIsNotExpertGroupOwnerAndKitVersionStatusIsUpdating_ThenThrowAccessDeniedException() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(deleteQuestionnairePort).delete(param.getKitVersionId(), param.getQuestionnaireId());

        assertDoesNotThrow(() -> service.deleteQuestionnaire(param));

        verify(deleteQuestionnairePort).delete(param.getKitVersionId(), param.getQuestionnaireId());
    }



    private DeleteQuestionnaireUseCase.Param createParam(Consumer<DeleteQuestionnaireUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteQuestionnaireUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteQuestionnaireUseCase.Param.builder()
            .kitVersionId(1L)
            .questionnaireId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
