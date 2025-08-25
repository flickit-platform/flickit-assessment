package org.flickit.assessment.core.application.service.measure;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MeasureMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeMeasureQuestionsServiceTest {

    @InjectMocks
    private GetAttributeMeasureQuestionsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAttributeQuestionsPort loadAttributeQuestionsPort;

    private final GetAttributeMeasureQuestionsUseCase.Param param =
        createParam(GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder::build);

    @Test
    void testGetAttributeMeasureQuestions_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestions(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort, loadAssessmentResultPort);
    }

    @Test
    void testGetAttributeMeasureQuestions_whenParamsAreValid_thenReturnQuestions() {
        var assessmentResult = AssessmentResultMother.validResult();
        var measure1 = MeasureMother.createMeasure();
        var measure2 = MeasureMother.createMeasure();
        var question1 = QuestionMother.withMeasure(measure1);
        var question2 = QuestionMother.withMeasure(measure1);
        var question3 = QuestionMother.withMeasure(measure2);
        var answer1 = AnswerMother.fullScore(question1.getId());
        var answer2 = AnswerMother.noScore(question2.getId());
        var portResult = List.of(new LoadAttributeQuestionsPort.Result(question1, answer1),
            new LoadAttributeQuestionsPort.Result(question2, answer2),
            new LoadAttributeQuestionsPort.Result(question3, null));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeQuestionsPort.loadAttributeMeasureQuestions(assessmentResult, param.getAttributeId(), param.getMeasureId()))
            .thenReturn(portResult);

        var result = service.getQuestions(param);

        assertThat(result.items()).zipSatisfy(portResult, (actual, expected) -> {
            assertEquals(expected.question().getId(), actual.question().id());
            assertEquals(expected.question().getIndex(), actual.question().index());
            assertEquals(expected.question().getTitle(), actual.question().title());
            assertEquals(expected.question().getAvgWeight(param.getAttributeId()), actual.question().weight());

            assertNotNull(expected.answer().getSelectedOption());
            assertEquals(expected.answer().getSelectedOption().getIndex(), actual.answer().index());
            assertEquals(expected.answer().getSelectedOption().getTitle(), actual.answer().title());
            assertEquals(expected.answer().getIsNotApplicable(), actual.answer().isNotApplicable());
            assertNotNull(actual.answer().gainedScore());
            assertNotNull(actual.answer().missedScore());
        });
    }

    private GetAttributeMeasureQuestionsUseCase.Param createParam(Consumer<GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeMeasureQuestionsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .measureId(3L)
            .currentUserId(UUID.randomUUID());
    }
}
