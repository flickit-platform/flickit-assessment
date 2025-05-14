package org.flickit.assessment.core.application.service.measure;

import org.flickit.assessment.core.application.port.out.measure.LoadMeasuresPort;
import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.MeasureMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateMeasureHelperTest {

    @InjectMocks
    private CalculateMeasureHelper helper;

    @Mock
    private LoadMeasuresPort loadMeasuresPort;

    @Test
    void testCalculateMeasure_whenParamIsValid_thenReturnResult() {
        var assessmentId = UUID.randomUUID();
        var measure1 = MeasureMother.createMeasure();
        var measure2 = MeasureMother.createMeasure();
        var answer1 = AnswerMother.fullScore(15L);
        var answer2 = AnswerMother.noScore(16L);
        var question1 = new CalculateMeasureHelper.QuestionDto(15L, 1, measure1.getId(), answer1);
        var question2 = new CalculateMeasureHelper.QuestionDto(16L, 1, measure1.getId(), answer2);
        var question3 = new CalculateMeasureHelper.QuestionDto(17L, 1, measure2.getId(), null);

        when(loadMeasuresPort.loadAll(List.of(measure1.getId(), measure2.getId()), assessmentId))
            .thenReturn(List.of(measure1, measure2));

        var result = helper.calculateMeasures(assessmentId, List.of(question1, question2, question3));

        assertEquals(2, result.size());

        assertEquals(measure1.getTitle(), result.getFirst().title());
        assertEquals(66.67, result.getFirst().impactPercentage());
        assertEquals(2, result.getFirst().maxPossibleScore());
        assertEquals(1, result.getFirst().gainedScore());
        assertEquals(1, result.getFirst().missedScore());
        assertEquals(33.33, result.getFirst().gainedScorePercentage());
        assertEquals(33.33, result.getFirst().missedScorePercentage());

        assertEquals(measure2.getTitle(), result.get(1).title());
        assertEquals(33.33, result.get(1).impactPercentage());
        assertEquals(1, result.get(1).maxPossibleScore());
        assertEquals(0, result.get(1).gainedScore());
        assertEquals(1, result.get(1).missedScore());
        assertEquals(0, result.get(1).gainedScorePercentage());
        assertEquals(33.33, result.get(1).missedScorePercentage());
    }

    @Test
    void testCalculateMeasure_whenQuestionsIsEmpty_thenReturnEmptyResult() {
        var assessmentId = UUID.randomUUID();

        when(loadMeasuresPort.loadAll(List.of(), assessmentId)).thenReturn(List.of());

        var result = helper.calculateMeasures(assessmentId, List.of());

        assertEquals(0, result.size());
    }
}
