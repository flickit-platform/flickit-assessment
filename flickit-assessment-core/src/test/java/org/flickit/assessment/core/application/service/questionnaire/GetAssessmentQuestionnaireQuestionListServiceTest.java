package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Param;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Result;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerListPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_QUESTIONNAIRE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentQuestionnaireQuestionListServiceTest {

    @InjectMocks
    private GetAssessmentQuestionnaireQuestionListService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadQuestionnaireQuestionListPort loadQuestionnaireQuestionListPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadQuestionsAnswerListPort loadQuestionsAnswerListPort;

    @Test
    void testGetAssessmentQuestionnaireQuestionList_InvalidCurrentUser_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), 12L, 10, 0, UUID.randomUUID());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionnaireQuestionList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionnaireQuestionListPort, loadAssessmentResultPort, loadQuestionsAnswerListPort);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_InvalidQuestionnaireId_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), 12L, 10, 0, UUID.randomUUID());
        Question question = QuestionMother.withOptions();
        PaginatedResponse<Question> expectedPaginatedResponse = new PaginatedResponse<>(
            List.of(question),
            0,
            1,
            "index",
            "asc",
            1
        );
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), assessmentResult.getKitVersionId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenThrow(new ResourceNotFoundException(GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_FOUND));

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getQuestionnaireQuestionList(param));
        assertEquals(GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_ValidParam_ValidResult() {
        Param param = new Param(UUID.randomUUID(), 12L, 10, 0, UUID.randomUUID());
        Question question = QuestionMother.withOptions();
        PaginatedResponse<Question> expectedPaginatedResponse = new PaginatedResponse<>(
            List.of(question),
            0,
            1,
            "index",
            "asc",
            1
        );
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        Answer answer = new Answer(UUID.randomUUID(), new AnswerOption(question.getOptions().getFirst().getId(), 2,
            null, question.getId(), null), question.getId(), 1, Boolean.FALSE);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), assessmentResult.getKitVersionId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenReturn(List.of(answer));

        PaginatedResponse<Result> result = service.getQuestionnaireQuestionList(param);

        assertEquals(expectedPaginatedResponse.getSize(), result.getSize());
        assertEquals(expectedPaginatedResponse.getTotal(), result.getTotal());
        assertEquals(expectedPaginatedResponse.getOrder(), result.getOrder());
        assertEquals(expectedPaginatedResponse.getPage(), result.getPage());
        assertEquals(expectedPaginatedResponse.getSort(), result.getSort());
        Result item = result.getItems().getFirst();
        assertEquals(question.getId(), item.id());
        assertEquals(question.getTitle(), item.title());
        assertEquals(question.getIndex(), item.index());
        assertEquals(question.getHint(), item.hint());
        assertEquals(question.getMayNotBeApplicable(), item.mayNotBeApplicable());
        assertNotNull(answer.getSelectedOption());
        assertEquals(answer.getSelectedOption().getId(), item.answer().selectedOption().id());
        assertEquals(question.getOptions().getFirst().getTitle(), item.answer().selectedOption().title());
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_NotApplicableAnswerValidParam_ValidResult() {
        Param param = new Param(UUID.randomUUID(), 12L, 10, 0, UUID.randomUUID());
        Question question = QuestionMother.withOptions();
        PaginatedResponse<Question> expectedPaginatedResponse = new PaginatedResponse<>(
            List.of(question),
            0,
            1,
            "index",
            "asc",
            1
        );
        Answer answer = new Answer(UUID.randomUUID(), new AnswerOption(question.getOptions().getFirst().getId(), 2, null, question.getId(), null), question.getId(), 1, Boolean.TRUE);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), assessmentResult.getKitVersionId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenReturn(List.of(answer));

        PaginatedResponse<Result> result = service.getQuestionnaireQuestionList(param);

        assertEquals(expectedPaginatedResponse.getSize(), result.getSize());
        assertEquals(expectedPaginatedResponse.getTotal(), result.getTotal());
        assertEquals(expectedPaginatedResponse.getOrder(), result.getOrder());
        assertEquals(expectedPaginatedResponse.getPage(), result.getPage());
        assertEquals(expectedPaginatedResponse.getSort(), result.getSort());
        Result item = result.getItems().getFirst();
        assertEquals(question.getId(), item.id());
        assertEquals(question.getTitle(), item.title());
        assertEquals(question.getIndex(), item.index());
        assertEquals(question.getHint(), item.hint());
        assertEquals(question.getMayNotBeApplicable(), item.mayNotBeApplicable());
        assertNull(item.answer().selectedOption());
        assertTrue(item.answer().isNotApplicable());
        assertNotNull(item.answer().confidenceLevel());
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_SelectedOptionIsNullAnswerValidParam_ValidResult() {
        Param param = new Param(UUID.randomUUID(), 12L, 10, 0, UUID.randomUUID());
        Question question = QuestionMother.withOptions();
        PaginatedResponse<Question> expectedPaginatedResponse = new PaginatedResponse<>(
            List.of(question),
            0,
            1,
            "index",
            "asc",
            1
        );
        Answer answer = new Answer(UUID.randomUUID(), null, question.getId(), 1, Boolean.FALSE);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), assessmentResult.getKitVersionId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenReturn(List.of(answer));

        PaginatedResponse<Result> result = service.getQuestionnaireQuestionList(param);

        assertEquals(expectedPaginatedResponse.getSize(), result.getSize());
        assertEquals(expectedPaginatedResponse.getTotal(), result.getTotal());
        assertEquals(expectedPaginatedResponse.getOrder(), result.getOrder());
        assertEquals(expectedPaginatedResponse.getPage(), result.getPage());
        assertEquals(expectedPaginatedResponse.getSort(), result.getSort());
        Result item = result.getItems().getFirst();
        assertEquals(question.getId(), item.id());
        assertEquals(question.getTitle(), item.title());
        assertEquals(question.getIndex(), item.index());
        assertEquals(question.getHint(), item.hint());
        assertEquals(question.getMayNotBeApplicable(), item.mayNotBeApplicable());
        assertFalse(item.answer().isNotApplicable());
        assertNull(item.answer().selectedOption());
        assertNull(item.answer().confidenceLevel());
    }
}
