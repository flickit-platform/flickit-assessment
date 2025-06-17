package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GetAssessmentNextQuestionnaireServiceTest {

    @InjectMocks
    private GetAssessmentNextQuestionnaireService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    private final GetAssessmentNextQuestionnaireUseCase.Param param = createParam(GetAssessmentNextQuestionnaireUseCase.Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = validResult();

    @Test
    void getAssessmentNextQuestionnaire_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getNextQuestionnaire(param));

        assertEquals(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadQuestionnairesPort);
    }

    @Test
    void getAssessmentNextQuestionnaire_whenQuestionnaireNotFound_thenThrowResourceNotFoundException() {
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnairesPort.loadQuestionnaireDetails(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(List.of());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getNextQuestionnaire(param));

        assertEquals(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_QUESTIONNAIRE_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void getAssessmentNextQuestionnaire_whenNextUnansweredQuestionnaireDoesNotExist_thenThrowResourceNotFoundException() {
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        List<LoadQuestionnairesPort.Result> questionnaires = List.of(new LoadQuestionnairesPort.Result(1L, 2, "title1", 10, 10),
            new LoadQuestionnairesPort.Result(2L, 1, "title2", 10, 10),
            new LoadQuestionnairesPort.Result(3L, 3, "title3", 10, 10),
            new LoadQuestionnairesPort.Result(4L, 4, "title4", 10, 10));
        when(loadQuestionnairesPort.loadQuestionnaireDetails(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(questionnaires);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getNextQuestionnaire(param));

        assertEquals(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_NEXT_QUESTIONNAIRE_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void getAssessmentNextQuestionnaire_whenParamAreValid_thenReturnNextQuestionnaire() {
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        List<LoadQuestionnairesPort.Result> questionnaires = List.of(new LoadQuestionnairesPort.Result(1L, 2, "title1", 10, 10),
            new LoadQuestionnairesPort.Result(2L, 1, "title2", 10, 10),
            new LoadQuestionnairesPort.Result(3L, 3, "title3", 10, 10),
            new LoadQuestionnairesPort.Result(4L, 4, "title4", 10, 8));
        when(loadQuestionnairesPort.loadQuestionnaireDetails(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(questionnaires);
        var expectedQuestionnaire = questionnaires.get(3);

        var result = service.getNextQuestionnaire(param);

        assertEquals(expectedQuestionnaire.id(), result.id());
        assertEquals(expectedQuestionnaire.title(), result.title());
        assertEquals(expectedQuestionnaire.index(), result.index());
    }

    private GetAssessmentNextQuestionnaireUseCase.Param createParam(Consumer<GetAssessmentNextQuestionnaireUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentNextQuestionnaireUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentNextQuestionnaireUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
