package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.question.DeleteQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.DeleteQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTION_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createActiveKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    private final DeleteQuestionUseCase.Param param = createParam(DeleteQuestionUseCase.Param.ParamBuilder::build);
    private KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testDeleteQuestion_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteQuestion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteQuestionPort, updateQuestionPort, loadQuestionPort);
    }

    @Test
    void testDeleteQuestion_whenCurrentUserIsExpertGroupOwnerAndKitVersionIsNotInUpdatingState_thenThrowValidationException() {
        kitVersion = createActiveKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());

        var throwable = assertThrows(ValidationException.class, () -> service.deleteQuestion(param));
        assertEquals(DELETE_QUESTION_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(deleteQuestionPort, updateQuestionPort, loadQuestionPort);
    }

    @Test
    void testDeleteQuestion_whenCurrentUserIsExpertGroupOwnerAndKitVersionIsInUpdatingState_thenDeleteQuestion() {
        var question = createQuestion();
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);

        service.deleteQuestion(param);

        verify(deleteQuestionPort).delete(param.getQuestionId(), param.getKitVersionId());
        verify(updateQuestionPort).reindexQuestionsAfter(question.getIndex(), question.getQuestionnaireId(), param.getKitVersionId());
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
