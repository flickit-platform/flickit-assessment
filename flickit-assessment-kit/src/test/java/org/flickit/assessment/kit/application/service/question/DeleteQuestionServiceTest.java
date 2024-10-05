package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.question.DeleteQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.DeleteQuestionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteQuestionServiceTest {

    @InjectMocks
    private DeleteQuestionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteQuestionPort deleteQuestionPort;

    private final UUID ownerId = UUID.randomUUID();
    private KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testDeleteQuestion_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(DeleteQuestionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteQuestion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteQuestionPort);
    }

    @Test
    void testDeleteQuestion_WhenCurrentUserIsExpertGroupOwnerAndKitVersionIsNotInUpdatingState_ThenThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        kitVersion = new KitVersion(1L,
                simpleKit(),
                KitVersionStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                ownerId,
                ownerId);
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteQuestion(param));

        assertEquals(DELETE_QUESTION_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(deleteQuestionPort);
    }

    @Test
    void testDeleteQuestion_WhenCurrentUserIsExpertGroupOwnerAndKitVersionIsInUpdatingState_ThenDeleteQuestion() {
        var param = createParam(b -> b.currentUserId(ownerId));
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(deleteQuestionPort).deleteQuestion(param.getQuestionId(), param.getKitVersionId());

        assertDoesNotThrow(() -> service.deleteQuestion(param));

        verify(deleteQuestionPort).deleteQuestion(param.getQuestionId(), param.getKitVersionId());
    }

    private DeleteQuestionUseCase.Param createParam(Consumer<DeleteQuestionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteQuestionUseCase.Param.builder()
                .kitVersionId(1L)
                .questionId(2L)
                .currentUserId(UUID.randomUUID());
    }
}
