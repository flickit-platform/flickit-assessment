package org.flickit.assessment.core.application.service.answer;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.port.in.answer.ApproveAssessmentAnswersUseCase;
import org.flickit.assessment.core.application.port.out.answer.ApproveAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ANSWERS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveAssessmentAnswersServiceTest {

    @InjectMocks
    private ApproveAssessmentAnswersService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ApproveAnswerPort approveAnswerPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAnswerPort loadAnswerPort;

    @Mock
    private CreateAnswerHistoryPort createAnswerHistoryPort;

    private final ApproveAssessmentAnswersUseCase.Param param = createParam(ApproveAssessmentAnswersUseCase.Param.ParamBuilder::build);

    @Test
    void testApproveAllAnswers_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ANSWERS))
                .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approveAllAnswers(param));

        assertThat(throwable.getMessage()).isEqualTo(COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(approveAnswerPort,
                loadAssessmentResultPort,
                loadAnswerPort,
                createAnswerHistoryPort);
    }

    @Test
    void testApproveAllAnswers_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ANSWERS))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.approveAllAnswers(param));
        assertThat(throwable.getMessage()).isEqualTo(COMMON_ASSESSMENT_RESULT_NOT_FOUND);

        verifyNoInteractions(approveAnswerPort,
                loadAnswerPort,
                createAnswerHistoryPort);
    }

    @Test
    void testApproveAllAnswers_whenParametersAreValid_thenSuccessfullyApprove() {
        var assessmentResult = AssessmentResultMother.validResult();
        var answerList = List.of(AnswerMother.fullScore(1), AnswerMother.partialScore(2, 1),
                AnswerMother.fullScore(1), AnswerMother.answerWithNotApplicableTrue(null),
                AnswerMother.answerWithNotApplicableFalse(null));
        //The last one answer should be filtered
        var filteredAnswerList = answerList.subList(0, answerList.size() - 1);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ANSWERS))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
                .thenReturn(Optional.of(assessmentResult));
        when(loadAnswerPort.loadAll(assessmentResult.getId(), AnswerStatus.UNAPPROVED))
                .thenReturn(answerList);

        service.approveAllAnswers(param);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnswerHistory>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(createAnswerHistoryPort).persistAll(argumentCaptor.capture(), eq(assessmentResult.getId()));
        assertThat(argumentCaptor.getValue()).zipSatisfy(filteredAnswerList, (actual, expected) -> {
            assertThat(actual.getAnswer().getAnswerStatus()).isEqualTo(AnswerStatus.APPROVED);
            assertThat(actual.getAnswer().getConfidenceLevelId()).isEqualTo(expected.getConfidenceLevelId());
            assertThat(actual.getAnswer().getSelectedOption()).isEqualTo(expected.getSelectedOption());
            assertThat(actual.getAnswer().getQuestionId()).isEqualTo(expected.getQuestionId());
            assertThat(actual.getAnswer().getQuestionId()).isEqualTo(expected.getQuestionId());
            assertThat(assessmentResult.getId()).isEqualTo(actual.getAssessmentResultId());
        });

        verify(approveAnswerPort).approveAll(assessmentResult.getId(), param.getCurrentUserId());
    }

    private ApproveAssessmentAnswersUseCase.Param createParam(Consumer<ApproveAssessmentAnswersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private ApproveAssessmentAnswersUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveAssessmentAnswersUseCase.Param.builder()
                .assessmentId(UUID.randomUUID())
                .currentUserId(UUID.randomUUID());
    }
}
