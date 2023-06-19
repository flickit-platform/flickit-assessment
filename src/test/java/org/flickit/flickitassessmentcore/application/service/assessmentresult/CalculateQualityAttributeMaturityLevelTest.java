package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswersByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.flickit.flickitassessmentcore.Constants.ANSWER_OPTION_IMPACT_VALUE4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CalculateQualityAttributeMaturityLevelTest {
    private final LoadQuestionsByQualityAttributePort loadQuestionsByQAId = Mockito.mock(LoadQuestionsByQualityAttributePort.class);
    private final LoadAnswerOptionImpactsByAnswerOptionPort loadAnswerOptionImpactsByAnswerOption = Mockito.mock(LoadAnswerOptionImpactsByAnswerOptionPort.class);
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKit = Mockito.mock(LoadMaturityLevelByKitPort.class);
    private final LoadAnswersByResultPort loadAnswersByResult = Mockito.mock(LoadAnswersByResultPort.class);
    private final LoadLevelCompetenceByMaturityLevelPort loadLevelCompetenceByMaturityLevel = Mockito.mock(LoadLevelCompetenceByMaturityLevelPort.class);
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    private final CalculateQualityAttributeMaturityLevel service = new CalculateQualityAttributeMaturityLevel(
        loadQuestionsByQAId,
        loadAnswerOptionImpactsByAnswerOption,
        loadMaturityLevelByKit,
        loadAnswersByResult,
        loadLevelCompetenceByMaturityLevel);

    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInMaturityLevel2_WillSucceed() {
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        QualityAttributeValue qav = service.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute());
        assertEquals(2, qav.getMaturityLevel().getValue());
    }

    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInMaturityLevel1_WillSucceed() {
        context.getOptionImpact1Q2().setValue(new BigDecimal(0));
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        QualityAttributeValue qav = service.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute());
        assertEquals(1, qav.getMaturityLevel().getValue());
        // Return to former state
        context.getOptionImpact1Q2().setValue(ANSWER_OPTION_IMPACT_VALUE4);
    }

    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInNoAnswerException_WillFail() {
        context.getAnswer2().setQuestion(null);
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        assertThrows(NoAnswerFoundException.class, () -> service.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute()));
        // Return to former state
        context.getAnswer2().setQuestion(context.getQuestion2());
    }

    private void doMocks() {
        doReturn(Set.of(context.getQuestion1(), context.getQuestion2())).when(loadQuestionsByQAId).loadQuestionsByQualityAttributeId(context.getQualityAttribute().getId());
        doReturn(Set.of(context.getOptionImpact2Q1())).when(loadAnswerOptionImpactsByAnswerOption).findAnswerOptionImpactsByAnswerOptionId(context.getOption2Q1().getId());
        doReturn(Set.of(context.getOptionImpact1Q2())).when(loadAnswerOptionImpactsByAnswerOption).findAnswerOptionImpactsByAnswerOptionId(context.getOption1Q2().getId());
        doReturn(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2())).when(loadMaturityLevelByKit).loadMaturityLevelByKitId(context.getKit().getId());
        doReturn(Set.of(context.getAnswer1(), context.getAnswer2())).when(loadAnswersByResult).loadAnswersByResultId(context.getResult().getId());
        doReturn(Set.of(context.getLevelCompetence1(), context.getLevelCompetence2())).when(loadLevelCompetenceByMaturityLevel).loadLevelCompetenceByMaturityLevelId(context.getMaturityLevel2().getId());
    }


}
