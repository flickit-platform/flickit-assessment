package org.flickit.assessment.core.application.service.questionnaire;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase.Param;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QuestionnaireListItemMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_QUESTIONNAIRE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testGetQuestionnaireList_InvalidCurrentUser_ThrowsAccessDeniedException() {
        Param param = new Param(UUID.randomUUID(), 10, 0, UUID.randomUUID());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetQuestionnaireList_assessmentResultNotFound_ThrowsResourceNotFoundException() {
        Param param = new Param(UUID.randomUUID(), 10, 0, UUID.randomUUID());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetQuestionnaireList_whenQuestionnairesDoesNotHaveIssues_ReturnListSuccessfully() {
        final int questionsCount = 10;
        final int answersCount = 9;
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(assessmentResult, param.getSize(), param.getPage());
        var questionnaires = List.of(QuestionnaireListItemMother.createWithoutIssuesByQuestionCountAndAnswerCount(questionsCount, answersCount),
            QuestionnaireListItemMother.createWithoutIssuesByQuestionCountAndAnswerCount(questionsCount, answersCount));
        var answeredWithLowConfidenceCount = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 0));
        var unresolvedCommentsCount = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 0));
        var answeredWithEvidence = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 0));
        var questionnaireIds = questionnaires.stream()
            .map(QuestionnaireListItem::id)
            .toList();

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
        when(countEvidencesPort.countUnresolvedComments(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(answeredWithEvidence);

        var actualResult = service.getAssessmentQuestionnaireList(param);
        var actualIssues = actualResult.getItems().stream().map(QuestionnaireListItem::issues).toList();
        var expectedIssues = loadPortResult.getItems().stream().map(QuestionnaireListItem::issues).toList();

        Assertions.assertThat(actualResult.getItems())
            .zipSatisfy(loadPortResult.getItems(), (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.answerCount(), actual.answerCount());
                assertEquals(expected.description(), actual.description());
                assertEquals(expected.index(), actual.index());
                assertEquals(expected.nextQuestion(), actual.nextQuestion());
                assertEquals(expected.progress(), actual.progress());
                assertEquals(expected.questionCount(), actual.questionCount());
            });
        Assertions.assertThat(actualIssues)
            .zipSatisfy(expectedIssues, (actual, expected) -> {
                assertEquals(questionsCount - answersCount, actual.unanswered());
                assertEquals(expected.answeredWithLowConfidence(), actual.answeredWithLowConfidence());
                assertEquals(expected.unresolvedComments(), actual.unresolvedComments());
                assertEquals(answersCount, actual.answeredWithoutEvidence());
            });

        assertEquals(loadPortResult.getSort(), actualResult.getSort());
        assertEquals(loadPortResult.getPage(), actualResult.getPage());
        assertEquals(loadPortResult.getTotal(), actualResult.getTotal());
        assertEquals(loadPortResult.getOrder(), actualResult.getOrder());
        assertEquals(loadPortResult.getSize(), actualResult.getSize());
    }

    @Test
    void testGetQuestionnaireList_whenQuestionnairesHaveIssues_ReturnListSuccessfully() {
        final int questionsCount = 10;
        final int answersCount = 7;
        final int lowConfidence = 2;
        final int unresolved = 4;
        final int withEvidences = 5;
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(assessmentResult, param.getSize(), param.getPage());
        var questionnaires = List.of(QuestionnaireListItemMother.createContainingIssuesByQuestionCountAndAnswerCount(questionsCount, answersCount),
            QuestionnaireListItemMother.createContainingIssuesByQuestionCountAndAnswerCount(questionsCount, answersCount));
        var answeredWithLowConfidenceCount = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> lowConfidence));
        var unresolvedCommentsCount = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> unresolved));
        var answeredWithEvidence = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> withEvidences));
        var questionnaireIds = questionnaires.stream()
            .map(QuestionnaireListItem::id)
            .toList();

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
        when(countEvidencesPort.countUnresolvedComments(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(answeredWithEvidence);

        var actualResult = service.getAssessmentQuestionnaireList(param);
        var actualIssues = actualResult.getItems().stream().map(QuestionnaireListItem::issues).toList();
        var expectedIssues = loadPortResult.getItems().stream().map(QuestionnaireListItem::issues).toList();
        Assertions.assertThat(actualResult.getItems())
            .zipSatisfy(loadPortResult.getItems(), (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.answerCount(), actual.answerCount());
                assertEquals(expected.description(), actual.description());
                assertEquals(expected.index(), actual.index());
                assertEquals(expected.nextQuestion(), actual.nextQuestion());
                assertEquals(expected.progress(), actual.progress());
                assertEquals(expected.questionCount(), actual.questionCount());
            });
        Assertions.assertThat(actualIssues)
            .zipSatisfy(expectedIssues, (actual, expected) -> {
                assertEquals(questionsCount - answersCount, actual.unanswered());
                assertEquals(expected.answeredWithLowConfidence(), actual.answeredWithLowConfidence());
                assertEquals(expected.unresolvedComments(), actual.unresolvedComments());
                assertEquals(answersCount - withEvidences, actual.answeredWithoutEvidence());
            });

        assertEquals(loadPortResult.getSort(), actualResult.getSort());
        assertEquals(loadPortResult.getPage(), actualResult.getPage());
        assertEquals(loadPortResult.getTotal(), actualResult.getTotal());
        assertEquals(loadPortResult.getOrder(), actualResult.getOrder());
        assertEquals(loadPortResult.getSize(), actualResult.getSize());
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
