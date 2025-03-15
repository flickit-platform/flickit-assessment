package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase.Param;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase.QuestionnaireParam;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.MeasureMother.measureFromQuestionnaire;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionnaireOrdersServiceTest {

    @InjectMocks
    private UpdateQuestionnaireOrdersService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateQuestionnairePort updateQuestionnairePort;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    @Mock
    private LoadMeasurePort loadMeasurePort;

    @Mock
    private UpdateMeasurePort updateMeasurePort;

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateQuestionnaireOrders_whenUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.changeOrders(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateQuestionnairePort, loadQuestionnairesPort, loadMeasurePort, updateMeasurePort);
    }

    @Test
    void testUpdateQuestionnaireOrders_whenParametersAreValid_thenUpdatesSuccessfully() {
        var questionnaire1 = questionnaireWithTitle("Questionnaire1");
        var questionnaire2 = questionnaireWithTitle("Questionnaire2");
        var measure1 = measureFromQuestionnaire(questionnaire1);
        var measure2 = measureFromQuestionnaire(questionnaire2);

        var param = createParam(b -> b.orders(
            List.of(new QuestionnaireParam(questionnaire1.getId(), 2),
                new QuestionnaireParam(questionnaire2.getId(), 1))));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());
        when(loadQuestionnairesPort.loadAll(param.getKitVersionId())).thenReturn(List.of(questionnaire1, questionnaire2));
        when(loadMeasurePort.loadAll(param.getKitVersionId())).thenReturn(List.of(measure1, measure2));

        service.changeOrders(param);
        var questionnairePortParamCaptor = ArgumentCaptor.forClass(UpdateQuestionnairePort.UpdateOrderParam.class);
        verify(updateQuestionnairePort, times(1)).updateOrders(questionnairePortParamCaptor.capture());

        assertEquals(param.getKitVersionId(), questionnairePortParamCaptor.getValue().kitVersionId());
        assertEquals(param.getCurrentUserId(), questionnairePortParamCaptor.getValue().lastModifiedBy());
        assertNotNull(questionnairePortParamCaptor.getValue().lastModificationTime());
        assertNotNull(questionnairePortParamCaptor.getValue().orders());
        assertEquals(param.getOrders().size(), questionnairePortParamCaptor.getValue().orders().size());
        assertEquals(param.getOrders().getFirst().getId(), questionnairePortParamCaptor.getValue().orders().getFirst().questionnaireId());
        assertEquals(param.getOrders().getFirst().getIndex(), questionnairePortParamCaptor.getValue().orders().getFirst().index());
        assertEquals(param.getOrders().getLast().getId(), questionnairePortParamCaptor.getValue().orders().getLast().questionnaireId());
        assertEquals(param.getOrders().getLast().getIndex(), questionnairePortParamCaptor.getValue().orders().getLast().index());

        var measurePortParamCaptor = ArgumentCaptor.forClass(UpdateMeasurePort.UpdateOrderParam.class);
        verify(updateMeasurePort, times(1)).updateOrders(measurePortParamCaptor.capture());

        assertEquals(param.getKitVersionId(), measurePortParamCaptor.getValue().kitVersionId());
        assertEquals(param.getCurrentUserId(), measurePortParamCaptor.getValue().lastModifiedBy());
        assertNotNull(measurePortParamCaptor.getValue().lastModificationTime());
        assertNotNull(measurePortParamCaptor.getValue().orders());
        assertEquals(param.getOrders().size(), measurePortParamCaptor.getValue().orders().size());
        assertEquals(measure1.getId(), measurePortParamCaptor.getValue().orders().getFirst().measureId());
        assertEquals(param.getOrders().getFirst().getIndex(), measurePortParamCaptor.getValue().orders().getFirst().index());
        assertEquals(measure2.getId(), measurePortParamCaptor.getValue().orders().getLast().measureId());
        assertEquals(param.getOrders().getLast().getIndex(), measurePortParamCaptor.getValue().orders().getLast().index());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .orders(List.of(
                new QuestionnaireParam(123L, 3),
                new QuestionnaireParam(124L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
