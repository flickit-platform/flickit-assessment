package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Param;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase.Result;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerListPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_QUESTIONNAIRE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.APPROVED;
import static org.flickit.assessment.core.application.domain.AnswerStatus.UNAPPROVED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentQuestionnaireQuestionListServiceTest {

    @InjectMocks
    private GetAssessmentQuestionnaireQuestionListService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadQuestionnaireQuestionListPort loadQuestionnaireQuestionListPort;

    @Mock
    private LoadQuestionsAnswerListPort loadQuestionsAnswerListPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    private final Question question = QuestionMother.withOptions();

    private final PaginatedResponse<Question> expectedPaginatedResponse = new PaginatedResponse<>(
        List.of(question),
        0,
        1,
        "index",
        "asc",
        1
    );

    @Test
    void testGetAssessmentQuestionnaireQuestionList_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        Param param = createParam(GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionnaireQuestionList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionnaireQuestionListPort, loadQuestionsAnswerListPort);
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_ValidParamsAndAnswerIsNotApplicableFalse_ValidResult() {
        Param param = createParam(GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder::build);

        Answer answer = new Answer(UUID.randomUUID(), new AnswerOption(question.getOptions().getFirst().getId(), 2,
            null, null), question.getId(), 1, Boolean.FALSE, APPROVED);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), param.getAssessmentId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenReturn(List.of(answer));

        PaginatedResponse<Result> result = service.getQuestionnaireQuestionList(param);
        //Assert Pagination params
        assertPaginationProperties(result);
        //Assert Question properties
        Result item = result.getItems().getFirst();
        assertQuestionProperties(item);
        //Assert Answer properties
        assertNotNull(answer.getSelectedOption());
        assertEquals(answer.getSelectedOption().getId(), item.answer().selectedOption().id());
        assertEquals(question.getOptions().getFirst().getTitle(), item.answer().selectedOption().title());
        assertTrue(item.answer().approved());
        //Assert Issues
        assertFalse(item.issues().isUnanswered());
        assertTrue(item.issues().isAnsweredWithLowConfidence());
        assertTrue(item.issues().isAnsweredWithoutEvidences());
        assertEquals(0, item.issues().unresolvedCommentsCount());
        assertFalse(item.issues().hasUnapprovedAnswer());

        verify(countEvidencesPort).countQuestionnaireQuestionsEvidences(param.getAssessmentId(), param.getQuestionnaireId());
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_ValidParamsAndAnswerIsNotApplicable_ValidResult() {
        Param param = createParam(GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder::build);
        Answer answer = new Answer(UUID.randomUUID(), new AnswerOption(question.getOptions().getFirst().getId(), 2,
            null, null), question.getId(), 3, Boolean.TRUE, UNAPPROVED);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), param.getAssessmentId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenReturn(List.of(answer));
        when(countEvidencesPort.countQuestionnaireQuestionsEvidences(param.getAssessmentId(), param.getQuestionnaireId()))
            .thenReturn(Map.of(question.getId(), 1));
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId(), param.getQuestionnaireId()))
            .thenReturn(Map.of(question.getId(), 2));

        PaginatedResponse<Result> result = service.getQuestionnaireQuestionList(param);
        //Assert Pagination params
        assertPaginationProperties(result);
        Result item = result.getItems().getFirst();
        //Assert Question properties
        assertQuestionProperties(item);
        //Assert Answer properties
        assertNull(item.answer().selectedOption());
        assertTrue(item.answer().isNotApplicable());
        assertNotNull(item.answer().confidenceLevel());
        assertFalse(item.answer().approved());
        //Assert Issues
        assertFalse(item.issues().isUnanswered());
        assertFalse(item.issues().isAnsweredWithLowConfidence());
        assertFalse(item.issues().isAnsweredWithoutEvidences());
        assertEquals(2, item.issues().unresolvedCommentsCount());
        assertTrue(item.issues().hasUnapprovedAnswer());
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_ValidParamsAndSelectedOptionIsNullAndNotApplicable_ValidResult() {
        Param param = createParam(GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder::build);
        Answer answer = new Answer(UUID.randomUUID(), null, question.getId(), 3, Boolean.TRUE, APPROVED);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), param.getAssessmentId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenReturn(List.of(answer));
        when(countEvidencesPort.countQuestionnaireQuestionsEvidences(param.getAssessmentId(), param.getQuestionnaireId()))
            .thenReturn(Map.of(question.getId(), 1));
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId(), param.getQuestionnaireId()))
            .thenReturn(Map.of());

        PaginatedResponse<Result> result = service.getQuestionnaireQuestionList(param);
        //Assert Pagination params
        assertPaginationProperties(result);
        Result item = result.getItems().getFirst();
        //Assert Question properties
        assertQuestionProperties(item);
        //Assert Answer properties
        assertNull(item.answer().selectedOption());
        assertTrue(item.answer().isNotApplicable());
        assertNotNull(item.answer().confidenceLevel());
        assertTrue(item.answer().approved());
        //Assert Issues
        assertFalse(item.issues().isUnanswered());
        assertFalse(item.issues().isAnsweredWithLowConfidence());
        assertFalse(item.issues().isAnsweredWithoutEvidences());
        assertEquals(0, item.issues().unresolvedCommentsCount());
        assertFalse(item.issues().hasUnapprovedAnswer());
    }

    @Test
    void testGetAssessmentQuestionnaireQuestionList_ValidParamsAndSelectedOptionIsNullAndIsNotApplicableFalse_ValidResult() {
        Param param = createParam(GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder::build);
        Answer answer = new Answer(UUID.randomUUID(), null, question.getId(), 1, Boolean.FALSE, null);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(), param.getAssessmentId(), param.getSize(), param.getPage()))
            .thenReturn(expectedPaginatedResponse);
        when(loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), List.of(question.getId())))
            .thenReturn(List.of(answer));
        when(countEvidencesPort.countQuestionnaireQuestionsEvidences(param.getAssessmentId(), param.getQuestionnaireId()))
            .thenReturn(Map.of());
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId(), param.getQuestionnaireId()))
            .thenReturn(Map.of(question.getId(), 1));

        PaginatedResponse<Result> result = service.getQuestionnaireQuestionList(param);
        //Assert Pagination params
        assertPaginationProperties(result);
        Result item = result.getItems().getFirst();
        //Assert Question properties
        assertQuestionProperties(item);
        //Assert Answer properties
        assertFalse(item.answer().isNotApplicable());
        assertNull(item.answer().selectedOption());
        assertNull(item.answer().confidenceLevel());
        assertNull(item.answer().approved());
        //Assert Issues
        assertTrue(item.issues().isUnanswered());
        assertFalse(item.issues().isAnsweredWithLowConfidence());
        assertFalse(item.issues().isAnsweredWithoutEvidences());
        assertEquals(1, item.issues().unresolvedCommentsCount());
        assertFalse(item.issues().hasUnapprovedAnswer());
    }

    private void assertPaginationProperties(PaginatedResponse<Result> result) {
        assertAll("Pagination Properties",
            () -> assertEquals(expectedPaginatedResponse.getSize(), result.getSize()),
            () -> assertEquals(expectedPaginatedResponse.getTotal(), result.getTotal()),
            () -> assertEquals(expectedPaginatedResponse.getOrder(), result.getOrder()),
            () -> assertEquals(expectedPaginatedResponse.getPage(), result.getPage()),
            () -> assertEquals(expectedPaginatedResponse.getSort(), result.getSort())
        );
    }

    private void assertQuestionProperties(Result item) {
        assertAll("Question Properties",
            () -> assertEquals(question.getId(), item.id()),
            () -> assertEquals(question.getTitle(), item.title()),
            () -> assertEquals(question.getIndex(), item.index()),
            () -> assertEquals(question.getHint(), item.hint()),
            () -> assertEquals(question.getMayNotBeApplicable(), item.mayNotBeApplicable())
        );
    }

    private GetAssessmentQuestionnaireQuestionListUseCase.Param createParam(Consumer<GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentQuestionnaireQuestionListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentQuestionnaireQuestionListUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionnaireId(0L)
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}
