package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Param;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Result;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionnaireAnswerListPort;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentQuestionnaireQuestionListServiceTest {

    @InjectMocks
    private GetAssessmentQuestionnaireQuestionListService service;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    private LoadQuestionnaireQuestionListPort loadQuestionnaireQuestionListPort;

    @Mock
    private LoadQuestionnaireAnswerListPort loadQuestionnaireAnswerListPort;

    @Test
    void testGetAssessmentQuestionnaireQuestionList_InvalidCurrentUser_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), 12L, 10, 0, UUID.randomUUID());
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getAssessmentQuestionnaireQuestionList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verifyNoInteractions(loadQuestionnaireQuestionListPort,
            loadQuestionnaireAnswerListPort);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_InvalidQuestionnaireId_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), 12L, 10, 0, UUID.randomUUID());

        PaginatedResponse<Question> expectedPaginatedResponse = new PaginatedResponse<>(
            List.of(QuestionMother.withOptions()),
            0,
            1,
            "index",
            "asc",
            1
        );

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionnaireAnswerListPort.loadQuestionnaireAnswers(param.getAssessmentId(), param.getQuestionnaireId(), param.getSize(), param.getPage()))
            .thenThrow(new ResourceNotFoundException(GET_ASSESSMENT_QUESTIONNAIRE_QUESTION_LIST_ASSESSMENT_ID_NOT_FOUND));

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentQuestionnaireQuestionList(param));
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
        Answer answer = new Answer(UUID.randomUUID(), new AnswerOption(question.getOptions().get(0).getId(), null, null, question.getId(), null), question.getId(), 1, Boolean.TRUE);

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionnaireAnswerListPort.loadQuestionnaireAnswers(param.getAssessmentId(), param.getQuestionnaireId(), param.getSize(), param.getPage()))
            .thenReturn(List.of(answer));

        PaginatedResponse<Result> result = service.getAssessmentQuestionnaireQuestionList(param);

        assertEquals(expectedPaginatedResponse.getSize(), result.getSize());
        assertEquals(expectedPaginatedResponse.getTotal(), result.getTotal());
        assertEquals(expectedPaginatedResponse.getOrder(), result.getOrder());
        assertEquals(expectedPaginatedResponse.getPage(), result.getPage());
        assertEquals(expectedPaginatedResponse.getSort(), result.getSort());
        Result item = result.getItems().get(0);
        assertEquals(question.getId(), item.id());
        assertEquals(question.getTitle(), item.title());
        assertEquals(question.getIndex(), item.index());
        assertEquals(question.getHint(), item.hint());
        assertEquals(question.getMayNotBeApplicable(), item.mayNotBeApplicable());
        assertEquals(answer.getSelectedOption().getId(), item.answer().id());
        assertEquals(question.getOptions().get(0).getTitle(), item.answer().caption());
    }
}
