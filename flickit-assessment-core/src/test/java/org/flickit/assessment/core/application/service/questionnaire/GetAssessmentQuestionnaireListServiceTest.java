package org.flickit.assessment.core.application.service.questionnaire;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
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
    private LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CountLowConfidenceAnswersPort countLowConfidenceAnswersPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    @Test
    void testGetQuestionnaireList_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        Param param = new Param(UUID.randomUUID(), 10, 0, UUID.randomUUID());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionnairesByAssessmentIdPort,
            loadAssessmentResultPort,
            countEvidencesPort,
            countLowConfidenceAnswersPort);
    }

    @Test
    void testGetQuestionnaireList_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        Param param = new Param(UUID.randomUUID(), 10, 0, UUID.randomUUID());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadQuestionnairesByAssessmentIdPort,
            countEvidencesPort,
            countLowConfidenceAnswersPort);
    }

    @Test
    void testGetQuestionnaireList_whenParamIsValid_thenReturnListSuccessfully() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(assessmentResult, param.getSize(), param.getPage());
        var questionnaireOne = createQuestionnaireListItem(10, 10);
        var questionnaireTwo = createQuestionnaireListItem(10, 5);
        var questionnaires = List.of(questionnaireOne, questionnaireTwo);

        var answeredWithLowConfidenceCount = Map.of(questionnaireOne.id(), 1, questionnaireTwo.id(), 3);
        var unresolvedCommentsCount = Map.of(questionnaireOne.id(), 3);
        var answeredWithEvidence = Map.of(questionnaireOne.id(), 3, questionnaireTwo.id(), 0);

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
        when(loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(portParam))
            .thenReturn(loadPortResult);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), questionnaireIds, ConfidenceLevel.SOMEWHAT_UNSURE))
            .thenReturn(answeredWithLowConfidenceCount);
        when(countEvidencesPort.countUnresolvedComments(assessmentResult.getAssessment().getId(), questionnaireIds))
            .thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(assessmentResult.getAssessment().getId(), questionnaireIds))
            .thenReturn(answeredWithEvidence);

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

        assertEquals(loadPortResult.getSort(), actualResult.getSort());
        assertEquals(loadPortResult.getPage(), actualResult.getPage());
        assertEquals(loadPortResult.getTotal(), actualResult.getTotal());
        assertEquals(loadPortResult.getOrder(), actualResult.getOrder());
        assertEquals(loadPortResult.getSize(), actualResult.getSize());

        var questionnaireOneIssues = actualResult.getItems().getFirst().issues();
        var questionnaireTwoIssues = actualResult.getItems().getLast().issues();

        assertEquals(0, questionnaireOneIssues.unanswered());
        assertEquals(1, questionnaireOneIssues.answeredWithLowConfidence());
        assertEquals(3, questionnaireOneIssues.unresolvedComments());
        assertEquals(7, questionnaireOneIssues.answeredWithoutEvidence());

        assertEquals(5, questionnaireTwoIssues.unanswered());
        assertEquals(3, questionnaireTwoIssues.answeredWithLowConfidence());
        assertEquals(0, questionnaireTwoIssues.unresolvedComments());
        assertEquals(5, questionnaireTwoIssues.answeredWithoutEvidence());
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
