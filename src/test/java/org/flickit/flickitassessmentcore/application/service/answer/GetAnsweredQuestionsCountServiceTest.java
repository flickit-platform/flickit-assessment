package org.flickit.flickitassessmentcore.application.service.answer;

import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentMother;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnsweredQuestionsCountUseCase;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnsweredQuestionsCountUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.out.answer.GetAnsweredQuestionsCountPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ANSWERED_QUESTIONS_COUNT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ANSWERED_QUESTIONS_COUNT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAnsweredQuestionsCountServiceTest {

    @InjectMocks
    private GetAnsweredQuestionsCountService service;

    @Mock
    private GetAnsweredQuestionsCountPort getAnsweredQuestionsCountPort;

    @Test
    void getAnsweredQuestionsCount_ValidResult() {
        var assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        Param param = new Param(assessmentId);

        when(getAnsweredQuestionsCountPort.getAnsweredQuestionsCountById(assessmentId))
            .thenReturn(new GetAnsweredQuestionsCountUseCase.Progress<UUID>(
                assessmentId, 5
            ));

        var result = service.getAnsweredQuestionsCount(param);

        ArgumentCaptor<UUID> answerPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getAnsweredQuestionsCountPort).getAnsweredQuestionsCountById(answerPortAssessmentId.capture());

        assertEquals(assessmentId, answerPortAssessmentId.getValue());
        verify(getAnsweredQuestionsCountPort, times(1)).getAnsweredQuestionsCountById(any());

        assertEquals(assessmentId, result.assessmentProgress().id());
        assertEquals(5, result.assessmentProgress().allAnswersCount());
    }

    @Test
    void getAnsweredQuestionsCount_NullAssessmentId() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> new Param(null));
        Assertions.assertThat(throwable).hasMessage("assessmentId: " + GET_ANSWERED_QUESTIONS_COUNT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void getAnsweredQuestionsCount_InValidAssessmentId() {
        var assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        Param param = new Param(assessmentId);

        when(getAnsweredQuestionsCountPort.getAnsweredQuestionsCountById(assessmentId))
            .thenThrow(new ResourceNotFoundException(GET_ANSWERED_QUESTIONS_COUNT_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAnsweredQuestionsCount(param));
        Assertions.assertThat(throwable).hasMessage(GET_ANSWERED_QUESTIONS_COUNT_ASSESSMENT_RESULT_NOT_FOUND);
    }
}
