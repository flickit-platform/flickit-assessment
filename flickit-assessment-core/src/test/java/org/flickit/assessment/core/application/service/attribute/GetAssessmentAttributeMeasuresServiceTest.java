package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributeMeasuresUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeQuestionsPort;
import org.flickit.assessment.core.application.port.out.measure.LoadMeasuresPort;
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
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAttributeMeasuresServiceTest {

    @InjectMocks
    private GetAssessmentAttributeMeasuresService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadMeasuresPort loadMeasuresPort;

    @Mock
    private LoadAttributeQuestionsPort loadAttributeQuestionsPort;

    private Param param = createParam(Param.ParamBuilder::build);

    @Test
    void testGetAssessmentAttributeMeasures_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentAttributeMeasures(param));
        assertThat(throwable.getMessage()).isEqualTo(COMMON_CURRENT_USER_NOT_ALLOWED);

        verifyNoInteractions(loadAssessmentResultPort, loadMeasuresPort, loadAttributeQuestionsPort);
    }

    @Test
    void testGetAssessmentAttributeMeasures_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentAttributeMeasures(param));
        assertThat(throwable.getMessage()).isEqualTo(COMMON_ASSESSMENT_RESULT_NOT_FOUND);

        verifyNoInteractions(loadMeasuresPort, loadAttributeQuestionsPort);
    }

    @Test
    void testGetAssessmentAttributeMeasures_whenParamIsValid_thenReturnResult() {
        var assessmentResult = AssessmentResultMother.validResult();
        var measure1 = MeasureMother.createMeasure();
        var measure2 = MeasureMother.createMeasure();
        var question1 = QuestionMother.withMeasure(measure1);
        var question2 = QuestionMother.withMeasure(measure1);
        var question3 = QuestionMother.withMeasure(measure2);
        var answer1 = AnswerMother.fullScore(question1.getId());
        var answer2 = AnswerMother.noScore(question2.getId());
        param = createParam(b -> b.attributeId(question1.getImpacts().getFirst().getAttributeId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURES))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadAttributeQuestionsPort.loadApplicableQuestions(param.getAssessmentId(), param.getAttributeId()))
            .thenReturn(List.of(new LoadAttributeQuestionsPort.Result(question1, answer1),
                new LoadAttributeQuestionsPort.Result(question2, answer2),
                new LoadAttributeQuestionsPort.Result(question3, null)));
        when(loadMeasuresPort.loadAll(List.of(measure1.getId(), measure2.getId()), assessmentResult.getKitVersionId()))
            .thenReturn(List.of(measure1, measure2));

        var result = service.getAssessmentAttributeMeasures(param);

        assertEquals(2, result.measures().size());

        assertEquals(measure1.getTitle(), result.measures().getFirst().title());
        assertEquals(66.67, result.measures().getFirst().impactPercentage());
        assertEquals(2, result.measures().getFirst().maxPossibleScore());
        assertEquals(1, result.measures().getFirst().gainedScore());
        assertEquals(1, result.measures().getFirst().missedScore());
        assertEquals(33.33, result.measures().getFirst().gainedScorePercentage());
        assertEquals(33.33, result.measures().getFirst().missedScorePercentage());

        assertEquals(measure2.getTitle(), result.measures().get(1).title());
        assertEquals(33.33, result.measures().get(1).impactPercentage());
        assertEquals(1, result.measures().get(1).maxPossibleScore());
        assertEquals(0, result.measures().get(1).gainedScore());
        assertEquals(1, result.measures().get(1).missedScore());
        assertEquals(0, result.measures().get(1).gainedScorePercentage());
        assertEquals(33.33, result.measures().get(1).missedScorePercentage());
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
