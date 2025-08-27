package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.CreateQuestionnaireUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateQuestionnaireServiceTest {

    @InjectMocks
    private CreateQuestionnaireService createQuestionnaireService;

    @Mock
    private CreateQuestionnairePort createQuestionnairePort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testCreateQuestionnaire_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> createQuestionnaireService.createQuestionnaire(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateQuestionnaire_WhenCurrentUserIsOwner_ThenCreateQuestionnaire() {
        long questionnaireId = 123L;
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(createQuestionnairePort.persist(any(Questionnaire.class), eq(param.getKitVersionId()), eq(param.getCurrentUserId())))
            .thenReturn(questionnaireId);

        long actualQuestionnaireId = createQuestionnaireService.createQuestionnaire(param);

        var createPortArgument = ArgumentCaptor.forClass(Questionnaire.class);
        verify(createQuestionnairePort, times(1)).persist(createPortArgument.capture(), anyLong(), any(UUID.class));
        assertEquals(param.getIndex(), createPortArgument.getValue().getIndex());
        assertEquals(param.getTitle(), createPortArgument.getValue().getTitle());
        assertEquals(param.getDescription(), createPortArgument.getValue().getDescription());
        assertEquals(param.getTranslations(), createPortArgument.getValue().getTranslations());

        assertEquals(questionnaireId, actualQuestionnaireId);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .index(1)
            .title("title")
            .description("description")
            .translations(Map.of("EN", new QuestionnaireTranslation("title", "desc")))
            .currentUserId(UUID.randomUUID());
    }
}
