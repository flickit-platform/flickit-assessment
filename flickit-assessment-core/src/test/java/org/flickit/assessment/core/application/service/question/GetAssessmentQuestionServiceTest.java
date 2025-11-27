package org.flickit.assessment.core.application.service.question;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.question.GetAssessmentQuestionUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AnswerOptionMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answerWithNotApplicableTrue;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionFour;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GetAssessmentQuestionServiceTest {

    @InjectMocks
    private GetAssessmentQuestionService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private LoadAnswerOptionPort loadAnswerOptionPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    @Mock
    private LoadAnswerHistoryPort loadAnswerHistoryPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final GetAssessmentQuestionUseCase.Param param = createParam(b -> b.assessmentId(assessmentResult.getAssessment().getId()));
    private final Question question = QuestionMother.withNoOption();
    private final List<AnswerOption> answerOptions = List.of(AnswerOptionMother.optionOne(), AnswerOptionMother.optionFour());

    @Test
    void getAssessmentQuestion_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            loadQuestionPort,
            loadAnswerOptionPort,
            loadAnswerPort,
            countEvidencesPort,
            loadAnswerHistoryPort);
    }

    @Test
    void getAssessmentQuestion_whenQuestionDoseNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getQuestion(param));
        assertEquals(GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAnswerOptionPort,
            loadAnswerPort,
            countEvidencesPort,
            loadAnswerHistoryPort);
    }
    @Test
    void getAssessmentQuestion_whenAnswerDoesNotExist_thenValidResult() {
        int answerHistoriesCount = 0;

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(loadAnswerOptionPort.loadAll(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(answerOptions);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(answerHistoriesCount);

        var result = service.getQuestion(param);
        assertEquals(answerHistoriesCount, result.counts().answerHistories());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(loadAnswerHistoryPort);
    }

    @Test
    void getAssessmentQuestion_whenAnswerDoesNotExistAndNotSubmittedYet_thenValidResult() {
        int evidencesCount = 2;
        int commentsCount = 3;

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(loadAnswerOptionPort.loadAll(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(answerOptions);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.empty());
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(evidencesCount);
        when(countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId())).thenReturn(commentsCount);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(false);

        var result = service.getQuestion(param);
        //Assert AnswerOptions
        assertThat(result.options())
            .zipSatisfy(answerOptions, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getIndex(), actual.index());
                assertEquals(expected.getTitle(), actual.title());
            });
        //Assert Answer
        assertNull(result.answer());
        //Assert Issues
        assertNull(result.issues());
        //Assert counts
        assertEquals(evidencesCount, result.counts().evidences());
        assertEquals(commentsCount, result.counts().comments());
        assertEquals(0, result.counts().answerHistories());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(loadAnswerHistoryPort);
    }

    @Test
    void getAssessmentQuestion_whenAnswerIsNullAndSubmittedBefore_thenValidResult() {
        int evidencesCount = 2;
        int commentsCount = 3;
        int answerHistoriesCount = 1;
        var answer = AnswerMother.answerWithNotApplicableTrueAndUnapprovedStatus(null);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(loadAnswerOptionPort.loadAll(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(answerOptions);
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(evidencesCount);
        when(countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId())).thenReturn(commentsCount);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(answer));
        when(loadAnswerHistoryPort.countQuestionAnswerHistories(assessmentResult.getId(), param.getQuestionId())).thenReturn(answerHistoriesCount);

        var result = service.getQuestion(param);
        //Assert AnswerOptions
        assertNull(answer.getSelectedOption());
        //Assert Answer
        assertEquals(answer.getConfidenceLevelId(), result.answer().confidenceLevel().getId());
        assertEquals(answer.getIsNotApplicable(), result.answer().isNotApplicable());
        assertEquals(AnswerStatus.UNAPPROVED.getId(), answer.getAnswerStatus().getId());
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

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
    }

    @Test
    void getAssessmentQuestion_whenNotApplicableAnswerExist_thenValidResult() {
        int evidencesCount = 1;
        int commentsCount = 4;
        int answerHistoriesCount = 1;
        var answerOption = optionFour();
        var answer = AnswerMother.answerWithNullNotApplicable(answerOption);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(loadAnswerOptionPort.loadAll(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(answerOptions);
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(evidencesCount);
        when(countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId())).thenReturn(commentsCount);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(false);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(answer));
        when(loadAnswerHistoryPort.countQuestionAnswerHistories(assessmentResult.getId(), param.getQuestionId())).thenReturn(answerHistoriesCount);

        var result = service.getQuestion(param);
        //Assert AnswerOptions
        assertNotNull(answer.getSelectedOption());
        assertThat(result.options())
            .zipSatisfy(answerOptions, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getIndex(), actual.index());
                assertEquals(expected.getTitle(), actual.title());
            });
        //Assert Answer
        assertNull(result.answer().confidenceLevel());
        assertNull(result.answer().isNotApplicable());
        assertNull(result.answer().selectedOption());
        //Assert Issues
        assertNull(result.issues());
        //Assert counts
        assertEquals(evidencesCount, result.counts().evidences());
        assertEquals(commentsCount, result.counts().comments());
        assertEquals(answerHistoriesCount, result.counts().answerHistories());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
    }

    @Test
    void getAssessmentQuestion_whenApplicableAnswerExist_thenValidResult() {
        int evidencesCount = 1;
        int commentsCount = 4;
        int answerHistoriesCount = 1;
        var answerOption = optionFour();
        var answer = answerWithNotApplicableTrue(answerOption);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId()))
            .thenReturn(Optional.of(question));
        when(loadAnswerOptionPort.loadAll(param.getQuestionId(), assessmentResult.getKitVersionId())).thenReturn(answerOptions);
        when(countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId())).thenReturn(evidencesCount);
        when(countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId())).thenReturn(commentsCount);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(false);
        when(loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())).thenReturn(Optional.of(answer));
        when(loadAnswerHistoryPort.countQuestionAnswerHistories(assessmentResult.getId(), param.getQuestionId())).thenReturn(answerHistoriesCount);

        var result = service.getQuestion(param);
        //Assert AnswerOptions
        assertNotNull(answer.getSelectedOption());
        assertThat(result.options())
            .zipSatisfy(answerOptions, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getIndex(), actual.index());
                assertEquals(expected.getTitle(), actual.title());
            });
        //Assert Answer
        assertEquals(answer.getConfidenceLevelId(), result.answer().confidenceLevel().getId());
        assertEquals(answer.getIsNotApplicable(), result.answer().isNotApplicable());
        assertEquals(AnswerStatus.APPROVED.getId(), answer.getAnswerStatus().getId());
        //Assert Issues
        assertNull(result.issues());
        //Assert counts
        assertEquals(evidencesCount, result.counts().evidences());
        assertEquals(commentsCount, result.counts().comments());
        assertEquals(answerHistoriesCount, result.counts().answerHistories());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
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
