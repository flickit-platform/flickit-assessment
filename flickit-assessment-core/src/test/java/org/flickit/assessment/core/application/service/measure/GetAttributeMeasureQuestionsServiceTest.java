package org.flickit.assessment.core.application.service.measure;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.QuestionMother.withAttributeAndMeasure;
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
    private LoadAttributeQuestionsPort loadAttributeQuestionsPort;

    private final GetAttributeMeasureQuestionsUseCase.Param param =
        createParam(GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder::build);

    @Test
    void testGetAttributeMeasureQuestions_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestions(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributeQuestionsPort);
    }

    @Test
    void testGetAttributeMeasureQuestions_whenParamsAreValid_thenReturnQuestions() {
        var q1 = withAttributeAndMeasure(param.getAttributeId(), param.getMeasureId(), 1); // full score → high
        var q2 = withAttributeAndMeasure(param.getAttributeId(), param.getMeasureId(), 1); // partial (0.4) → low
        var q3 = withAttributeAndMeasure(param.getAttributeId(), param.getMeasureId(), 2); // no score → low
        var q4 = withAttributeAndMeasure(param.getAttributeId(), param.getMeasureId(), 3); // null answer → low

        var a1 = AnswerMother.fullScore(q1.getId());        // value = 1.0
        var a2 = AnswerMother.partialScore(q2.getId(), 0.4); // value = 0.4
        var a3 = AnswerMother.noScore(q3.getId());          // value = 0.0

        var portResult = List.of(
            new LoadAttributeQuestionsPort.Result(q1, a1),
            new LoadAttributeQuestionsPort.Result(q2, a2),
            new LoadAttributeQuestionsPort.Result(q3, a3),
            new LoadAttributeQuestionsPort.Result(q4, null)
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS)).thenReturn(true);
        when(loadAttributeQuestionsPort.loadApplicableMeasureQuestions(param.getAssessmentId(), param.getAttributeId(), param.getMeasureId())).thenReturn(portResult);

        var result = service.getQuestions(param);

        // Expected subsets based on scoring rule
        var expectedHigh = List.of(portResult.get(0));            // q1 (fullScore)
        var expectedLow = List.of(portResult.get(3),          // q4 (null)
            portResult.get(2),               // q3 (noScore)
            portResult.get(1)              // q2 (partial 0.4)
        );

        // Assert counts
        assertThat(result.highScores()).hasSize(expectedHigh.size());
        assertThat(result.lowScores()).hasSize(expectedLow.size());

        // Assert high scores
        assertThat(result.highScores()).zipSatisfy(expectedHigh, (actual, expected) -> {
            assertEquals(expected.question().getId(), actual.question().id());
            assertEquals(expected.question().getIndex(), actual.question().index());
            assertEquals(expected.question().getTitle(), actual.question().title());

            assertNotNull(expected.answer());
            assertNotNull(expected.answer().getSelectedOption());
            assertEquals(expected.answer().getSelectedOption().getIndex(), actual.answer().index());
            assertEquals(expected.answer().getSelectedOption().getTitle(), actual.answer().title());
            assertThat(actual.answer().gainedScore()).isGreaterThan(actual.answer().missedScore());
        });

        // Assert low scores
        assertThat(result.lowScores()).zipSatisfy(expectedLow, (actual, expected) -> {
            assertEquals(expected.question().getId(), actual.question().id());
            assertEquals(expected.question().getIndex(), actual.question().index());
            assertEquals(expected.question().getTitle(), actual.question().title());

            if (expected.answer() != null && expected.answer().getSelectedOption() != null) {
                assertEquals(expected.answer().getSelectedOption().getIndex(), actual.answer().index());
                assertEquals(expected.answer().getSelectedOption().getTitle(), actual.answer().title());
                assertThat(actual.answer().gainedScore()).isLessThanOrEqualTo(actual.answer().missedScore());
            } else {
                // null answer case
                assertNull(actual.answer().index());
                assertNull(actual.answer().title());
                assertThat(actual.answer().gainedScore()).isZero();
                assertThat(actual.answer().missedScore()).isGreaterThanOrEqualTo(0.0);
            }
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
