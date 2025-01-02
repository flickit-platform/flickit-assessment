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
    void testGetQuestionnaireList_whenAssessmentDoesNotHaveIssues_ReturnListSuccessfully() {
        Param param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(param.getAssessmentId(), assessmentResult, param.getSize(), param.getPage());
        var questionnaires = List.of(QuestionnaireListItemMother.createWithoutIssues(),
            QuestionnaireListItemMother.createWithoutIssues());

        ArrayList<Long> questionnaireIds = questionnaires.stream()
            .map(QuestionnaireListItem::id)
            .collect(Collectors.toCollection(ArrayList::new));
        var countQuestionsWithLowConfidence = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 0));
        var countUnresolvedComment = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 0));
        var countWithNoEvidence = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 0));

        var expectedResult = new PaginatedResponse<>(
            questionnaires,
            0,
            10,
            "index",
            "asc",
            1);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(portParam))
            .thenReturn(expectedResult);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countByQuestionnaireIdWithConfidenceLessThan(assessmentResult.getId(), questionnaireIds, ConfidenceLevel.SOMEWHAT_UNSURE))
            .thenReturn(countQuestionsWithLowConfidence);
        when(countEvidencesPort.countQuestionnairesUnresolvedComments(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(countUnresolvedComment);
        when(countEvidencesPort.countQuestionnairesQuestionsHavingEvidence(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(countWithNoEvidence);

        var actualResult = service.getAssessmentQuestionnaireList(param);
        var actualIssues = actualResult.getItems().stream().map(QuestionnaireListItem::issues).toList();
        var expectedIssues = expectedResult.getItems().stream().map(QuestionnaireListItem::issues).toList();

        Assertions.assertThat(actualResult.getItems())
            .zipSatisfy(expectedResult.getItems(), (actual, expected) -> {
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
                assertEquals(0, actual.answeredWithLowConfidence());
                assertEquals(0, actual.unanswered());
                assertEquals(0, actual.unresolvedComments());
                assertEquals(10, actual.answeredWithoutEvidence());
            });
    }

    @Test
    void testGetQuestionnaireList_whenAssessmentHasIssues_ReturnListSuccessfully() {
        Param param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(param.getAssessmentId(), assessmentResult, param.getSize(), param.getPage());
        var questionnaires = List.of(QuestionnaireListItemMother.createWithIssues(),
            QuestionnaireListItemMother.createWithIssues());
        var countQuestionsWithLowConfidence = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 2));
        var countWithNoEvidence = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 5));
        var countUnresolvedComment = questionnaires.stream().collect(Collectors.toMap(QuestionnaireListItem::id, id -> 4));

        ArrayList<Long> questionnaireIds = questionnaires.stream()
            .map(QuestionnaireListItem::id)
            .collect(Collectors.toCollection(ArrayList::new));
        var expectedResult = new PaginatedResponse<>(
            questionnaires,
            0,
            10,
            "index",
            "asc",
            1);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(portParam))
            .thenReturn(expectedResult);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countByQuestionnaireIdWithConfidenceLessThan(assessmentResult.getId(), questionnaireIds, ConfidenceLevel.SOMEWHAT_UNSURE))
            .thenReturn(countQuestionsWithLowConfidence);
        when(countEvidencesPort.countQuestionnairesUnresolvedComments(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(countUnresolvedComment);
        when(countEvidencesPort.countQuestionnairesQuestionsHavingEvidence(assessmentResult.getAssessment().getId(), assessmentResult.getKitVersionId(), questionnaireIds))
            .thenReturn(countWithNoEvidence);

        var actualResult = service.getAssessmentQuestionnaireList(param);
        var actualIssues = actualResult.getItems().stream().map(QuestionnaireListItem::issues).toList();
        var expectedIssues = expectedResult.getItems().stream().map(QuestionnaireListItem::issues).toList();
        Assertions.assertThat(actualResult.getItems())
            .zipSatisfy(expectedResult.getItems(), (actual, expected) -> {
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
                assertEquals(3, actual.unanswered());
                assertEquals(2, actual.answeredWithLowConfidence());
                assertEquals(2, actual.answeredWithoutEvidence());
                assertEquals(4, actual.unresolvedComments());
            });
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
