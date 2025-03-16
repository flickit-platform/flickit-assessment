package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.question.CreateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.MeasureMother.measureFromQuestionnaire;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateQuestionServiceTest {

    @InjectMocks
    private CreateQuestionService createQuestionService;

    @Mock
    private CreateQuestionPort createQuestionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadQuestionnairePort loadQuestionnairePort;

    @Mock
    private LoadMeasurePort loadMeasurePort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());
    private final Questionnaire questionnaire = QuestionnaireMother.questionnaireWithTitle("Questionnaire");
    private final Measure measure = measureFromQuestionnaire(questionnaire);

    @Test
    void testCreateQuestionService_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        CreateQuestionUseCase.Param param = createParam(CreateQuestionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> createQuestionService.createQuestion(param));

        verifyNoInteractions(loadQuestionnairePort, loadMeasurePort, createQuestionPort);
    }

    @Test
    void testCreateQuestionService_WhenCurrentUserIsExpertGroupOwner_ThenCreateQuestion() {
        long questionId = 1L;
        CreateQuestionUseCase.Param param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionnairePort.load(param.getQuestionnaireId(), param.getKitVersionId())).thenReturn(questionnaire);
        when(loadMeasurePort.loadByCode(questionnaire.getCode(), param.getKitVersionId())).thenReturn(measure);
        when(createQuestionPort.persist(any(CreateQuestionPort.Param.class))).thenReturn(questionId);

        long actualQuestionId = createQuestionService.createQuestion(param);

        assertEquals(questionId, actualQuestionId);

        var outPortParam = ArgumentCaptor.forClass(CreateQuestionPort.Param.class);
        verify(createQuestionPort).persist(outPortParam.capture());
        assertNotNull(outPortParam.getValue());
        assertEquals(param.getKitVersionId(), outPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), outPortParam.getValue().index());
        assertEquals(param.getTitle(), outPortParam.getValue().title());
        assertEquals(param.getHint(), outPortParam.getValue().hint());
        assertEquals(param.getMayNotBeApplicable(), outPortParam.getValue().mayNotBeApplicable());
        assertEquals(param.getAdvisable(), outPortParam.getValue().advisable());
        assertEquals(param.getQuestionnaireId(), outPortParam.getValue().questionnaireId());
        assertEquals(measure.getId(), outPortParam.getValue().measureId());
        assertEquals(param.getCurrentUserId(), outPortParam.getValue().createdBy());
    }

    private CreateQuestionUseCase.Param createParam(Consumer<CreateQuestionUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private CreateQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return CreateQuestionUseCase.Param.builder()
            .kitVersionId(1L)
            .index(1)
            .title("abc")
            .hint("new hint")
            .mayNotBeApplicable(true)
            .advisable(false)
            .questionnaireId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
