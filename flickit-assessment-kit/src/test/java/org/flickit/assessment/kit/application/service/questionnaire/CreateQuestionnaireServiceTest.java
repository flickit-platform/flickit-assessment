package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.CreateQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateQuestionnaireServiceTest {

    @InjectMocks
    private CreateQuestionnaireService createQuestionnaireService;

    @Mock
    private CreateQuestionnairePort createQuestionnairePort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private final UUID ownerId = UUID.randomUUID();
    private final AssessmentKit kit = AssessmentKitMother.simpleKit();

    @Test
    void testCreateQuestionnaire_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(CreateQuestionnaireUseCase.Param.ParamBuilder::build);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> createQuestionnaireService.createQuestionnaire(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateQuestionnaire_WhenCurrentUserIsOwner_ThenCreateQuestionnaire() {
        long questionnaireId = 123L;
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createQuestionnairePort.persist(any(Questionnaire.class), anyLong(), any(UUID.class))).thenReturn(questionnaireId);

        long actualQuestionnaireId = createQuestionnaireService.createQuestionnaire(param);
        assertEquals(questionnaireId, actualQuestionnaireId);
    }

    private CreateQuestionnaireUseCase.Param createParam(Consumer<CreateQuestionnaireUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateQuestionnaireUseCase.Param.ParamBuilder paramBuilder() {
        return CreateQuestionnaireUseCase.Param.builder()
            .kitId(1L)
            .index(1)
            .title("title")
            .description("description")
            .currentUserId(UUID.randomUUID());
    }
}
