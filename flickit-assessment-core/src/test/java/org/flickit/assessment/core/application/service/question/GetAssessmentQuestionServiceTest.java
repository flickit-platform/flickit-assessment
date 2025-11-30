package org.flickit.assessment.core.application.service.question;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.question.GetAssessmentQuestionUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_QUESTIONNAIRE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answerWithNullNotApplicable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GetAssessmentQuestionServiceTest {

    @InjectMocks
    private GetAssessmentQuestionService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    @Mock
    private LoadAnswerHistoryPort loadAnswerHistoryPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final GetAssessmentQuestionUseCase.Param param = createParam(b -> b.assessmentId(assessmentResult.getAssessment().getId()));
    private final Question question = QuestionMother.withOptions();

    @Test
    void getAssessmentQuestion_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadQuestionPort,
            loadAnswerPort,
            countEvidencesPort,
            loadAnswerHistoryPort);
    }

    @Test
    void getAssessmentQuestion_whenQuestionDoesNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestion(param));
        assertEquals(GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAnswerPort,
            loadAnswerHistoryPort,
            countEvidencesPort);
    }

    @Test
    void getAssessmentQuestion_whenAnswerDoesNotExistAndUserDoesNotHaveViewIssuePermission_thenReturnResultWithNullAnswerAndIssues() {
        int evidencesCount = 2;
        int commentsCount = 3;

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(evidencesCount);
        when(countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId())).thenReturn(commentsCount);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(false);

        var result = service.getQuestion(param);

        assertNotNull(result);
        assertQuestionAndOptions(result);

        assertNull(result.answer());

        assertNull(result.issues());

        assertEquals(0, result.counts().answerHistories());
        assertEquals(evidencesCount, result.counts().evidences());
        assertEquals(commentsCount, result.counts().comments());

        verifyNoInteractions(loadAnswerHistoryPort);
    }

    @Test
    void getAssessmentQuestion_whenAnswerIsNotApplicableAndUnapproved_thenReturnValidResult() {
        int evidencesCount = 2;
        int commentsCount = 3;
        int answerHistoriesCount = 1;
        var answer = AnswerMother.answerWithNotApplicableTrueAndUnapprovedStatus(null);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(evidencesCount);
        when(countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId())).thenReturn(commentsCount);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(answer));
        when(loadAnswerHistoryPort.countQuestionAnswerHistories(assessmentResult.getId(), param.getQuestionId())).thenReturn(answerHistoriesCount);

        var result = service.getQuestion(param);

        assertQuestionAndOptions(result);

        assertNull(answer.getSelectedOption());
        assertEquals(answer.getConfidenceLevelId(), result.answer().confidenceLevel().getId());
        assertTrue(result.answer().isNotApplicable());
        assertFalse(result.answer().approved());

        //Assert Issues
        assertFalse(result.issues().isUnanswered());
        assertTrue(result.issues().isAnsweredWithLowConfidence());
        assertFalse(result.issues().isAnsweredWithoutEvidences());
        assertEquals(commentsCount, result.counts().comments());
        assertTrue(result.issues().hasUnapprovedAnswer());
        //Assert counts
        assertEquals(evidencesCount, result.counts().evidences());
        assertEquals(commentsCount, result.counts().comments());
        assertEquals(answerHistoriesCount, result.counts().answerHistories());
    }

    @Test
    void getAssessmentQuestion_whenApplicableAnswerExists_thenReturnValidResult() {
        int evidencesCount = 1;
        int commentsCount = 4;
        int answerHistoriesCount = 1;
        var answerOption = question.getOptions().getFirst();
        var answer = answerWithNullNotApplicable(answerOption);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(evidencesCount);
        when(countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId())).thenReturn(commentsCount);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(answer));
        when(loadAnswerHistoryPort.countQuestionAnswerHistories(assessmentResult.getId(), param.getQuestionId())).thenReturn(answerHistoriesCount);

        var result = service.getQuestion(param);

        assertQuestionAndOptions(result);

        assertNotNull(answer.getSelectedOption());
        assertEquals(answerOption.getId(), result.answer().selectedOption().id());
        assertEquals(answerOption.getIndex(), result.answer().selectedOption().index());
        assertEquals(answerOption.getTitle(), result.answer().selectedOption().title());
        assertEquals(answer.getConfidenceLevelId(), result.answer().confidenceLevel().getId());
        assertNull(result.answer().isNotApplicable());
        assertTrue(result.answer().approved());

        //Assert Issues
        assertFalse(result.issues().isUnanswered());
        assertTrue(result.issues().isAnsweredWithLowConfidence());
        assertFalse(result.issues().isAnsweredWithoutEvidences());
        assertEquals(commentsCount, result.counts().comments());
        assertFalse(result.issues().hasUnapprovedAnswer());
        //Assert counts
        assertEquals(evidencesCount, result.counts().evidences());
        assertEquals(commentsCount, result.counts().comments());
        assertEquals(answerHistoriesCount, result.counts().answerHistories());
    }

    private void assertQuestionAndOptions(GetAssessmentQuestionUseCase.Result result) {
        assertEquals(question.getId(), result.id());
        assertEquals(question.getTitle(), result.title());
        assertEquals(question.getIndex(), result.index());
        assertEquals(question.getHint(), result.hint());
        assertEquals(question.getMayNotBeApplicable(), result.mayNotBeApplicable());

        assertThat(result.options())
            .zipSatisfy(question.getOptions(), (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getIndex(), actual.index());
                assertEquals(expected.getTitle(), actual.title());
            });
    }

    private GetAssessmentQuestionUseCase.Param createParam(Consumer<GetAssessmentQuestionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentQuestionUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionId(0L)
            .currentUserId(UUID.randomUUID());
    }
}
