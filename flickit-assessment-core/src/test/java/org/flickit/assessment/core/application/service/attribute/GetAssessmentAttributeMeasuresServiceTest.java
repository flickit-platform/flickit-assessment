package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributeMeasuresUseCase.Param;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.flickit.assessment.core.application.service.measure.CalculateMeasureHelper;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.MeasureMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAttributeMeasuresServiceTest {

    @InjectMocks
    private GetAssessmentAttributeMeasuresService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CalculateMeasureHelper calculateMeasureHelper;

    @Mock
    private LoadAttributeQuestionsPort loadAttributeQuestionsPort;

    private Param param = createParam(Param.ParamBuilder::build);

    @Test
    void testGetAssessmentAttributeMeasures_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentAttributeMeasures(param));
        assertThat(throwable.getMessage()).isEqualTo(COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(calculateMeasureHelper, loadAttributeQuestionsPort);
    }

    @Test
    void testGetAssessmentAttributeMeasures_whenParamIsValid_thenReturnResult() {
        var measure = MeasureMother.createMeasure();
        var question = QuestionMother.withMeasure(measure);
        var answer = AnswerMother.fullScore(question.getId());
        param = createParam(b -> b.attributeId(question.getImpacts().getFirst().getAttributeId()));
        var questionDto = new CalculateMeasureHelper.QuestionDto(question.getId(), question.getAvgWeight(param.getAttributeId()), measure.getId(), answer);
        var measureDto = new CalculateMeasureHelper.MeasureDto(measure.getTitle(),
            100.0,
            90.0,
            80.0,
            70.0,
            60.0,
            50.0);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            .thenReturn(true);
        when(loadAttributeQuestionsPort.loadApplicableQuestions(param.getAssessmentId(), param.getAttributeId()))
            .thenReturn(List.of(new LoadAttributeQuestionsPort.Result(question, answer)));
        when(calculateMeasureHelper.calculateMeasures(param.getAssessmentId(), List.of(questionDto)))
            .thenReturn(List.of(measureDto));

        var result = service.getAssessmentAttributeMeasures(param);

        assertEquals(1, result.measures().size());

        assertEquals(measureDto.title(), result.measures().getFirst().title());
        assertEquals(measureDto.impactPercentage(), result.measures().getFirst().impactPercentage());
        assertEquals(measureDto.maxPossibleScore(), result.measures().getFirst().maxPossibleScore());
        assertEquals(measureDto.gainedScore(), result.measures().getFirst().gainedScore());
        assertEquals(measureDto.missedScore(), result.measures().getFirst().missedScore());
        assertEquals(measureDto.gainedScorePercentage(), result.measures().getFirst().gainedScorePercentage());
        assertEquals(measureDto.missedScorePercentage(), result.measures().getFirst().missedScorePercentage());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changes) {
        Param.ParamBuilder paramBuilder = paramBuilder();
        changes.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(554L)
            .sort("impact_percentage")
            .order("desc")
            .currentUserId(UUID.randomUUID());
    }
}
