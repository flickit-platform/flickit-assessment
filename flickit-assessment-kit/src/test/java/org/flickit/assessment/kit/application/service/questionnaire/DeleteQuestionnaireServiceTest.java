package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionnaire.DeleteQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.DeleteMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.DeleteQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.DELETE_QUESTIONNAIRE_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createActiveKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.MeasureMother.measureFromQuestionnaire;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private LoadQuestionnairePort loadQuestionnairePort;

    @Mock
    private LoadMeasurePort loadMeasurePort;

    @Mock
    private DeleteQuestionnairePort deleteQuestionnairePort;

    @Mock
    private DeleteMeasurePort deleteMeasurePort;

    UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testDeleteQuestionnaire_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(DeleteQuestionnaireUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteQuestionnaire(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionnairePort,
            loadMeasurePort,
            deleteQuestionnairePort,
            deleteMeasurePort);
    }

    @Test
    void testDeleteQuestionnaire_WhenCurrentUserIsExpertGroupOwnerAndKitVersionStatusIsNotUpdating_ThenThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        KitVersion activeKitVersion = createActiveKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(activeKitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(activeKitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteQuestionnaire(param));
        assertEquals(DELETE_QUESTIONNAIRE_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(loadQuestionnairePort,
            loadMeasurePort,
            deleteQuestionnairePort,
            deleteMeasurePort);
    }

    @Test
    void testDeleteQuestionnaire_WhenCurrentUserIsNotExpertGroupOwnerAndKitVersionStatusIsUpdating_ThenThrowAccessDeniedException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        var questionnaire = questionnaireWithTitle("questionnaire");
        var measure = measureFromQuestionnaire(questionnaire);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(deleteQuestionnairePort).delete(param.getQuestionnaireId(), param.getKitVersionId());
        when(loadQuestionnairePort.load(param.getKitVersionId(), param.getQuestionnaireId())).thenReturn(questionnaire);
        when(loadMeasurePort.loadByCode(param.getKitVersionId(), questionnaire.getCode())).thenReturn(measure);

        service.deleteQuestionnaire(param);

        verify(deleteQuestionnairePort).delete(param.getQuestionnaireId(), param.getKitVersionId());
        verify(deleteMeasurePort).delete(measure.getId(), param.getKitVersionId());
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
