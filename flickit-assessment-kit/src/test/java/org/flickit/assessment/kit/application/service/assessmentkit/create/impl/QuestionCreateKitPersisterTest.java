package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.flickit.assessment.kit.test.fixture.application.QuestionnaireMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.MaturityLevelDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionDslModelMother;
import org.flickit.assessment.kit.test.fixture.application.dsl.QuestionImpactDslModelMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext.*;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionImpactMother.createAnswerOptionImpact;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AttributeMother.createAttribute;
import static org.flickit.assessment.kit.test.fixture.application.Constants.*;
import static org.flickit.assessment.kit.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother.createQuestionImpact;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.flickit.assessment.kit.test.fixture.application.dsl.AnswerOptionDslModelMother.answerOptionDslModel;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionCreateKitPersisterTest {

    @InjectMocks
    private QuestionCreateKitPersister persister;
    @Mock
    private CreateQuestionPort createQuestionPort;
    @Mock
    private CreateQuestionImpactPort createQuestionImpactPort;
    @Mock
    private CreateAnswerOptionImpactPort createAnswerOptionImpactPort;
    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;
    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    @Test
    void testOrder() {
        Assertions.assertEquals(5, persister.order());
    }

    @Test
    void testPersist_ValidInputs_SaveQuestionAndItsRelatedEntities() {
        Long kitVersionId = 1L;
        long answerRangeId = 1L;
        UUID currentUserId = UUID.randomUUID();

        var levelTwo = levelTwo();
        var questionnaire = QuestionnaireMother.questionnaireWithTitle(QUESTIONNAIRE_TITLE1);
        var question = createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, Boolean.FALSE, Boolean.TRUE, 153L, questionnaire.getId());
        var attribute = createAttribute(ATTRIBUTE_CODE1, ATTRIBUTE_TITLE1, 1, "", 1);
        var impact = createQuestionImpact(attribute.getId(), levelTwo.getId(), 1, question.getId());
        var answerOption1 = createAnswerOption(question.getAnswerRangeId(), OPTION_TITLE, OPTION_INDEX1);
        var answerOption2 = createAnswerOption(question.getAnswerRangeId(), OPTION_TITLE, OPTION_INDEX2);
        var optionImpact1 = createAnswerOptionImpact(answerOption1.getId(), 0);
        var optionImpact2 = createAnswerOptionImpact(answerOption2.getId(), 1);
        impact.setOptionImpacts(List.of(optionImpact1, optionImpact2));
        question.setOptions(List.of(answerOption1, answerOption2));
        question.setImpacts(List.of(impact));
        questionnaire.setQuestions(List.of(question));

        var dslMaturityLevelTwo = MaturityLevelDslModelMother.domainToDslModel(levelTwo());
        var dslAnswerOption1 = answerOptionDslModel(1, OPTION_TITLE, OPTION_VALUE1);
        var dslAnswerOption2 = answerOptionDslModel(2, OPTION_TITLE, OPTION_VALUE2);
        List<AnswerOptionDslModel> dslAnswerOptionList = List.of(dslAnswerOption1, dslAnswerOption2);
        Map<Integer, Double> optionsIndexToValueMap = new HashMap<>();
        optionsIndexToValueMap.put(dslAnswerOption1.getIndex(), 0D);
        optionsIndexToValueMap.put(dslAnswerOption2.getIndex(), 1D);
        var dslImpact = QuestionImpactDslModelMother.questionImpactDslModel(ATTRIBUTE_CODE1, dslMaturityLevelTwo, null, optionsIndexToValueMap, 1);
        var dslQuestion = QuestionDslModelMother.questionDslModel(QUESTION_CODE1, 1, QUESTION_TITLE1, null, "c-" + QUESTIONNAIRE_TITLE1, List.of(dslImpact), dslAnswerOptionList, Boolean.FALSE, Boolean.TRUE);
        AssessmentKitDslModel dslModel = AssessmentKitDslModel.builder()
            .questions(List.of(dslQuestion))
            .build();

        var createQuestionParam = new CreateQuestionPort.Param(
            dslQuestion.getCode(),
            dslQuestion.getTitle(),
            dslQuestion.getIndex(),
            dslQuestion.getDescription(),
            dslQuestion.isMayNotBeApplicable(),
            dslQuestion.isAdvisable(),
            kitVersionId,
            questionnaire.getId(),
            answerRangeId,
            currentUserId
        );

        var createAnswerRangeParam = new CreateAnswerRangePort.Param(kitVersionId, null, false, currentUserId);

        when(createAnswerRangePort.persist(createAnswerRangeParam)).thenReturn(answerRangeId);
        when(createQuestionPort.persist(createQuestionParam)).thenReturn(question.getId());
        var createOption1Param = new CreateAnswerOptionPort.Param(answerOption1.getTitle(), answerOption1.getIndex(), answerRangeId, OPTION_VALUE1, kitVersionId, currentUserId);
        var createOption2Param = new CreateAnswerOptionPort.Param(answerOption2.getTitle(), answerOption2.getIndex(), answerRangeId, OPTION_VALUE2, kitVersionId, currentUserId);
        when(createAnswerOptionPort.persist(createOption1Param)).thenReturn(answerOption1.getId());
        when(createAnswerOptionPort.persist(createOption2Param)).thenReturn(answerOption2.getId());

        var createImpact = new QuestionImpact(
            null,
            attribute.getId(),
            levelTwo.getId(),
            dslImpact.getWeight(),
            kitVersionId,
            question.getId(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            currentUserId,
            currentUserId
        );
        when(createQuestionImpactPort.persist(createImpact)).thenReturn(impact.getId());

        var createOptionImpact1Param = new CreateAnswerOptionImpactPort.Param(impact.getId(), optionImpact1.getOptionId(), optionImpact1.getValue(), kitVersionId, currentUserId);
        var createOptionImpact2Param = new CreateAnswerOptionImpactPort.Param(impact.getId(), optionImpact2.getOptionId(), optionImpact2.getValue(), kitVersionId, currentUserId);
        when(createAnswerOptionImpactPort.persist(createOptionImpact1Param)).thenReturn(optionImpact1.getId());
        when(createAnswerOptionImpactPort.persist(createOptionImpact2Param)).thenReturn(optionImpact2.getId());

        CreateKitPersisterContext context = new CreateKitPersisterContext();
        context.put(KEY_QUESTIONNAIRES, Map.of(questionnaire.getCode(), questionnaire.getId()));
        context.put(KEY_ATTRIBUTES, Map.of(attribute.getCode(), attribute.getId()));
        context.put(KEY_MATURITY_LEVELS, Map.of(levelTwo.getCode(), levelTwo.getId()));

        persister.persist(context, dslModel, kitVersionId, currentUserId);

        // TODO: assert?
    }
}
