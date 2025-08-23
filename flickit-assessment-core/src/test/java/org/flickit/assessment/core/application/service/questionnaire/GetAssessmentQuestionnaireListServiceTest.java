package org.flickit.assessment.core.application.service.questionnaire;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersPort;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_QUESTIONNAIRE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.QuestionnaireListItemMother.createQuestionnaireListItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentQuestionnaireListServiceTest {

    @InjectMocks
    private GetAssessmentQuestionnaireListService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadQuestionnairesPort loadQuestionnairesPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CountLowConfidenceAnswersPort countLowConfidenceAnswersPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    @Mock
    private CountAnswersPort countAnswersPort;

    @Test
    void testGetQuestionnaireList_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionnairesPort,
            loadAssessmentResultPort,
            countEvidencesPort,
            countLowConfidenceAnswersPort,
            countAnswersPort);
    }

    @Test
    void testGetQuestionnaireList_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadQuestionnairesPort,
            countEvidencesPort,
            countLowConfidenceAnswersPort,
            countAnswersPort);
    }

    @Test
    void testGetQuestionnaireList_whenParamIsValid_thenReturnListSuccessfully() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var portParam = new LoadQuestionnairesPort.Param(assessmentResult, param.getSize(), param.getPage());
        var questionnaireOne = createQuestionnaireListItem(10, 10);
        var questionnaireTwo = createQuestionnaireListItem(10, 5);
        var questionnaires = List.of(questionnaireOne, questionnaireTwo);

        var answeredWithLowConfidenceCount = Map.of(questionnaireOne.id(), 1, questionnaireTwo.id(), 3);
        var unresolvedCommentsCount = Map.of(questionnaireOne.id(), 3);
        var answeredWithEvidence = Map.of(questionnaireOne.id(), 3, questionnaireTwo.id(), 0);
        var unapprovedAnswers = Map.of(questionnaireOne.id(), 4);

        var questionnaireIds = Set.of(questionnaireOne.id(), questionnaireTwo.id());

        var loadPortResult = new PaginatedResponse<>(
            questionnaires,
            0,
            10,
            "index",
            "asc",
            2);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadQuestionnairesPort.loadAllByAssessmentId(portParam))
            .thenReturn(loadPortResult);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), questionnaireIds, ConfidenceLevel.SOMEWHAT_UNSURE))
            .thenReturn(answeredWithLowConfidenceCount);
        when(countEvidencesPort.countUnresolvedComments(assessmentResult.getAssessment().getId(), questionnaireIds))
            .thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(assessmentResult.getAssessment().getId(), questionnaireIds))
            .thenReturn(answeredWithEvidence);
        when(countAnswersPort.countUnapprovedAnswers(assessmentResult.getId(), questionnaireIds))
            .thenReturn(unapprovedAnswers);

        var actualResult = service.getAssessmentQuestionnaireList(param);

        assertNotNull(actualResult.getItems());
        assertEquals(questionnaires.size(), actualResult.getItems().size());

        Assertions.assertThat(actualResult.getItems())
            .zipSatisfy(loadPortResult.getItems(), (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.answerCount(), actual.answerCount());
                assertEquals(expected.description(), actual.description());
                assertEquals(expected.index(), actual.index());
                assertEquals(expected.nextQuestion(), actual.nextQuestion());
                assertEquals(expected.progress(), actual.progress());
                assertEquals(expected.questionCount(), actual.questionCount());
                assertNotNull(actual.issues());
            });

        Assertions.assertThat(actualResult)
            .extracting(PaginatedResponse::getSort, PaginatedResponse::getPage, PaginatedResponse::getTotal, PaginatedResponse::getOrder, PaginatedResponse::getSize)
            .containsExactly(loadPortResult.getSort(), loadPortResult.getPage(), loadPortResult.getTotal(), loadPortResult.getOrder(), loadPortResult.getSize());

        var questionnaireOneIssues = actualResult.getItems().getFirst().issues();
        var questionnaireTwoIssues = actualResult.getItems().getLast().issues();

        assertEquals(0, questionnaireOneIssues.unanswered());
        assertEquals(1, questionnaireOneIssues.answeredWithLowConfidence());
        assertEquals(3, questionnaireOneIssues.unresolvedComments());
        assertEquals(7, questionnaireOneIssues.answeredWithoutEvidence());
        assertEquals(4, questionnaireOneIssues.unapprovedAnswers());

        assertEquals(5, questionnaireTwoIssues.unanswered());
        assertEquals(3, questionnaireTwoIssues.answeredWithLowConfidence());
        assertEquals(0, questionnaireTwoIssues.unresolvedComments());
        assertEquals(5, questionnaireTwoIssues.answeredWithoutEvidence());
        assertEquals(0, questionnaireTwoIssues.unapprovedAnswers());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .page(0)
            .size(50)
            .currentUserId(UUID.randomUUID());
    }
}
