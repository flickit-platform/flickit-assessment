package org.flickit.assessment.kit.application.service.measure;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.measure.GetKitMeasureDetailUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.test.fixture.application.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_MEASURE_DETAIL_MEASURE_ID_NOT_FOUND;
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createReusableAnswerRangeWithTwoOptions;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother.questionnaireWithTitle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitMeasureDetailServiceTest {

    @InjectMocks
    private GetKitMeasureDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Mock
    private LoadMeasurePort loadMeasurePort;

    @Mock
    private LoadQuestionsPort loadQuestionsPort;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    @Mock
    private LoadAnswerRangesPort loadAnswerRangesPort;

    private final GetKitMeasureDetailUseCase.Param param = createParam(GetKitMeasureDetailUseCase.Param.ParamBuilder::build);
    private final ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
    private final long kitVersionId = 123L;

    @Test
    void testGetKitMeasureDetail_whenCurrentUserIsNotExpertGroupMember_thenThrowAccessDeniedException() {
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getKitMeasureDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadActiveKitVersionIdPort,
            loadMeasurePort,
            loadQuestionsPort,
            loadQuestionnairesPort,
            loadAnswerRangesPort);
    }

    @Test
    void testGetKitMeasureDetail_whenMeasureIdNotFound_thenThrowResourceNotFoundException() {
        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadMeasurePort.load(param.getMeasureId(), kitVersionId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getKitMeasureDetail(param));
        assertEquals(GET_KIT_MEASURE_DETAIL_MEASURE_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadQuestionsPort,
            loadQuestionnairesPort,
            loadAnswerRangesPort);
    }

    @Test
    void testGetKitMeasureDetail_whenParamsAreValid_thenReturnResult() {
        var measure = MeasureMother.measureWithTitle("Measure");
        var answerRanges = List.of(AnswerRangeMother.createAnswerRangeWithFourOptions(), createReusableAnswerRangeWithTwoOptions());
        var questionnaires = List.of(questionnaireWithTitle("Questionnaire1"), questionnaireWithTitle("Questionnaire2"));
        var questions = List.of(createQuestion(answerRanges.getFirst().getId(), questionnaires.getLast().getId()),
            createQuestion(answerRanges.getLast().getId(), questionnaires.getFirst().getId()),
            createQuestion(answerRanges.getFirst().getId(), questionnaires.getLast().getId()));
        questions.getFirst().setOptions(answerRanges.getFirst().getAnswerOptions());
        questions.get(1).setOptions(answerRanges.getLast().getAnswerOptions());
        questions.getLast().setOptions(answerRanges.getFirst().getAnswerOptions());

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId())).thenReturn(true);
        when(loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId())).thenReturn(kitVersionId);
        when(loadMeasurePort.load(param.getMeasureId(), kitVersionId)).thenReturn(Optional.of(measure));
        when(loadQuestionsPort.loadAllByMeasureIdAndKitVersionId(measure.getId(), kitVersionId)).thenReturn(questions);
        when(loadQuestionnairesPort.loadByKitId(param.getKitId())).thenReturn(questionnaires);
        when(loadAnswerRangesPort.loadAll(kitVersionId)).thenReturn(answerRanges);

        var result = service.getKitMeasureDetail(param);
        assertEquals(measure.getTitle(), result.title());
        assertEquals(measure.getDescription(), result.description());
        assertEquals(questions.size(), result.questionsCount());
        assertEquals(measure.getTranslations(), result.translations());
        assertThat(result.questions())
            .zipSatisfy(questions, (actual, expected) -> {
                assertEquals(expected.getTitle(), actual.title());
                assertEquals(expected.getAnswerRangeId(), actual.answerRange().id());
                Assertions.assertNotNull(actual.answerRange().title());
                assertEquals(expected.getQuestionnaireId(), actual.questionnaire().id());
                Assertions.assertNotNull(actual.questionnaire().title());
                assertThat(actual.options())
                    .zipSatisfy(expected.getOptions(), (actualOptions, expectedOption) -> {
                        assertEquals(expectedOption.getId(), actualOptions.id());
                        assertEquals(expectedOption.getTitle(), actualOptions.title());
                        assertEquals(expectedOption.getIndex(), actualOptions.index());
                        assertEquals(expectedOption.getValue(), actualOptions.value());
                    });
            });
    }

    private GetKitMeasureDetailUseCase.Param createParam(Consumer<GetKitMeasureDetailUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetKitMeasureDetailUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitMeasureDetailUseCase.Param.builder()
            .kitId(1L)
            .measureId(10L)
            .currentUserId(UUID.randomUUID());
    }
}
