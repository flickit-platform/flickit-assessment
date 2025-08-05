package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.QUESTIONNAIRE_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GetAssessmentNextQuestionnaireServiceTest {

    @InjectMocks
    private GetAssessmentNextQuestionnaireService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    private GetAssessmentNextQuestionnaireUseCase.Param param = createParam(GetAssessmentNextQuestionnaireUseCase.Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = validResult();

    @Test
    void getNextQuestionnaire_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getNextQuestionnaire(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadQuestionnairesPort,
            loadQuestionPort);
    }

    @Test
    void getNextQuestionnaire_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getNextQuestionnaire(param));

        assertEquals(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadQuestionnairesPort,
            loadQuestionPort);
    }

    @Test
    void getNextQuestionnaire_whenQuestionnaireNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnairesPort.loadQuestionnairesProgress(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(List.of());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getNextQuestionnaire(param));

        assertEquals(QUESTIONNAIRE_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadQuestionPort);
    }

    @Test
    void getNextQuestionnaire_whenNextUnansweredQuestionnaireDoesNotExist_thenReturnNotFoundResult() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        var resultMap = List.of(new LoadQuestionnairesPort.Result(1L, 2, "title1", 10, 10),
            new LoadQuestionnairesPort.Result(2L, 1, "title2", 10, 10),
            new LoadQuestionnairesPort.Result(3L, 3, "title3", 10, 10),
            new LoadQuestionnairesPort.Result(4L, 4, "title4", 10, 10));
        when(loadQuestionnairesPort.loadQuestionnairesProgress(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(resultMap);

        var result = service.getNextQuestionnaire(param);

        assertEquals(GetAssessmentNextQuestionnaireUseCase.Result.NotFound.INSTANCE, result);

        verifyNoInteractions(loadQuestionPort);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void getNextQuestionnaire_whenParamsAreValid_thenReturnNextQuestionnaire(long currentQuestionnaireId) {
        param = paramBuilder().questionnaireId(currentQuestionnaireId).build();
        var resultMap = List.of(new LoadQuestionnairesPort.Result(1L, 2, "title1", 10, 10),
            new LoadQuestionnairesPort.Result(2L, 1, "title2", 10, 10),
            new LoadQuestionnairesPort.Result(3L, 3, "title3", 10, 8),
            new LoadQuestionnairesPort.Result(4L, 4, "title4", 10, 10));
        var expectedQuestionnaire = resultMap.get(2);
        int nextQuestionIndex = 5;

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnairesPort.loadQuestionnairesProgress(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(resultMap);
        when(loadQuestionPort.loadNextUnansweredQuestionIndex(expectedQuestionnaire.id(), assessmentResult.getId()))
            .thenReturn(nextQuestionIndex);

        var result = (GetAssessmentNextQuestionnaireUseCase.Result.Found) service.getNextQuestionnaire(param);
        assertNotNull(result);
        assertEquals(expectedQuestionnaire.id(), result.id());
        assertEquals(expectedQuestionnaire.index(), result.index());
        assertEquals(expectedQuestionnaire.title(), result.title());
        assertEquals(nextQuestionIndex, result.questionIndex());

        verify(loadQuestionPort, atMostOnce()).loadNextUnansweredQuestionIndex(anyLong(), any(UUID.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void getNextQuestionnaire_whenThereAreTwoUnansweredQuestionnaires_thenReturnNextQuestionnaire(long currentQuestionnaireId) {
        param = paramBuilder().questionnaireId(currentQuestionnaireId).build();
        var resultMap = List.of(new LoadQuestionnairesPort.Result(1L, 1, "title1", 10, 10),
            new LoadQuestionnairesPort.Result(2L, 2, "title2", 10, 9),
            new LoadQuestionnairesPort.Result(3L, 3, "title3", 10, 8),
            new LoadQuestionnairesPort.Result(4L, 4, "title4",10, 10));
        Map<Long, Long> expectedQuestionnaireIdToNextQuestionnaireIdMap = Map.of(1L, 2L, 2L, 3L, 3L, 2L, 4L, 2L);
        Map<Long, Integer> expectedQuestionnaireIdToNextQuestinIndex = Map.of(2L, 5, 3L, 1);
        var expectedQuestionnaireId = expectedQuestionnaireIdToNextQuestionnaireIdMap.get(currentQuestionnaireId);
        var expectedQuestionnaire = resultMap.stream()
            .filter(q -> q.id() == expectedQuestionnaireId)
            .findFirst()
            .orElseThrow();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnairesPort.loadQuestionnairesProgress(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(resultMap);
        when(loadQuestionPort.loadNextUnansweredQuestionIndex(expectedQuestionnaireId, assessmentResult.getId()))
            .thenReturn(expectedQuestionnaireIdToNextQuestinIndex.get(expectedQuestionnaireId));

        var result = (GetAssessmentNextQuestionnaireUseCase.Result.Found) service.getNextQuestionnaire(param);
        assertNotNull(result);
        assertEquals(expectedQuestionnaire.id(), result.id());
        assertEquals(expectedQuestionnaire.index(), result.index());
        assertEquals(expectedQuestionnaire.title(), result.title());
        assertEquals(expectedQuestionnaireIdToNextQuestinIndex.get(expectedQuestionnaireId), result.questionIndex());

        verify(loadQuestionPort, atMostOnce()).loadNextUnansweredQuestionIndex(anyLong(), any(UUID.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void getNextQuestionnaire_whenAllQuestionnairesAreAnswered_thenReturnNextQuestionnaire(long currentQuestionnaireId) {
        param = paramBuilder().questionnaireId(currentQuestionnaireId).build();
        var resultMap = List.of(new LoadQuestionnairesPort.Result(1L, 1, "title1",  10, 1),
            new LoadQuestionnairesPort.Result(2L, 2, "title2", 10, 9),
            new LoadQuestionnairesPort.Result(3L, 3, "title3", 10, 8),
            new LoadQuestionnairesPort.Result(4L, 4, "title4", 10, 7));
        Map<Long, Long> expectedQuestionnaireIdToNextQuestionnaireIdMap = Map.of(1L, 2L, 2L, 3L, 3L, 4L, 4L, 1L);
        Map<Long, Integer> expectedQuestionnaireIdToNextQuestinIndex = Map.of(1L, 7, 2L, 5, 3L, 1, 4L, 1);
        var expectedQuestionnaireId = expectedQuestionnaireIdToNextQuestionnaireIdMap.get(currentQuestionnaireId);
        var expectedQuestionnaire = resultMap.stream()
            .filter(q -> q.id() == expectedQuestionnaireId)
            .findFirst()
            .orElseThrow();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnairesPort.loadQuestionnairesProgress(assessmentResult.getKitVersionId(), assessmentResult.getId()))
            .thenReturn(resultMap);
        when(loadQuestionPort.loadNextUnansweredQuestionIndex(expectedQuestionnaireId, assessmentResult.getId()))
            .thenReturn(expectedQuestionnaireIdToNextQuestinIndex.get(expectedQuestionnaireId));

        var result = (GetAssessmentNextQuestionnaireUseCase.Result.Found) service.getNextQuestionnaire(param);
        assertNotNull(result);
        assertEquals(expectedQuestionnaire.id(), result.id());
        assertEquals(expectedQuestionnaire.index(), result.index());
        assertEquals(expectedQuestionnaire.title(), result.title());
        assertEquals(expectedQuestionnaireIdToNextQuestinIndex.get(expectedQuestionnaireId), result.questionIndex());

        verify(loadQuestionPort, atMostOnce()).loadNextUnansweredQuestionIndex(anyLong(), any(UUID.class));
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
