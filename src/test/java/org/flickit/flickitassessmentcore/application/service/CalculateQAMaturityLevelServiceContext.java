package org.flickit.flickitassessmentcore.application.service;

import lombok.Getter;
import org.flickit.flickitassessmentcore.domain.*;

import static org.flickit.flickitassessmentcore.Constants.*;
import static org.flickit.flickitassessmentcore.Constants.MATURITY_LEVEL_TITLE2;
import static org.flickit.flickitassessmentcore.Utils.*;

@Getter
public class CalculateQAMaturityLevelServiceContext {

    private final Assessment assessment;
    private final AssessmentResult result;
    private final AssessmentKit kit;
    private final AssessmentSubject subject;
    private final Questionnaire questionnaire;
    private final QualityAttribute qualityAttribute;
    private final QualityAttributeValue qualityAttributeValue;
    private final Question question1;
    private final Question question2;
    private final QuestionImpact questionImpact1;
    private final QuestionImpact questionImpact2;
    private final Answer answer1;
    private final Answer answer2;
    private final AnswerOption option1Q1;
    private final AnswerOption option2Q1;
    private final AnswerOption option3Q1;
    private final AnswerOption option1Q2;
    private final AnswerOption option2Q2;
    private final AnswerOption option3Q2;
    private final AnswerOptionImpact optionImpact1Q1;
    private final AnswerOptionImpact optionImpact2Q1;
    private final AnswerOptionImpact optionImpact3Q1;
    private final AnswerOptionImpact optionImpact1Q2;
    private final AnswerOptionImpact optionImpact2Q2;
    private final AnswerOptionImpact optionImpact3Q2;
    private final AssessmentColor color;
    private final MaturityLevel maturityLevel1;
    private final MaturityLevel maturityLevel2;
    private final LevelCompetence levelCompetence1;
    private final LevelCompetence levelCompetence2;

    public CalculateQAMaturityLevelServiceContext() {
        assessment = createAssessment();
        kit = createAssessmentKit();
        result = createAssessmentResult();
        subject = createAssessmentSubject();
        questionnaire = createQuestionnaire();
        qualityAttribute = createQualityAttribute();
        qualityAttributeValue = createQualityAttributeValue();
        question1 = createQuestion(QUESTION_ID1, QUESTION_TITLE1, QUESTION_DESCRIPTION1);
        question2 = createQuestion(QUESTION_ID2, QUESTION_TITLE2, QUESTION_DESCRIPTION2);
        questionImpact1 = createQuestionImpact(QUESTION_IMPACT_ID1, QUESTION_IMPACT_LEVEL1, QUESTION_IMPACT_WEIGHT1);
        questionImpact2 = createQuestionImpact(QUESTION_IMPACT_ID2, QUESTION_IMPACT_LEVEL2, QUESTION_IMPACT_WEIGHT2);
        answer1 = createAnswer();
        answer2 = createAnswer();
        option1Q1 = createAnswerOption(ANSWER_OPTION_ID1, ANSWER_OPTION_CAPTION1, ANSWER_OPTION_VALUE1);
        option2Q1 = createAnswerOption(ANSWER_OPTION_ID2, ANSWER_OPTION_CAPTION2, ANSWER_OPTION_VALUE2);
        option3Q1 = createAnswerOption(ANSWER_OPTION_ID3, ANSWER_OPTION_CAPTION3, ANSWER_OPTION_VALUE3);
        option1Q2 = createAnswerOption(ANSWER_OPTION_ID4, ANSWER_OPTION_CAPTION4, ANSWER_OPTION_VALUE4);
        option2Q2 = createAnswerOption(ANSWER_OPTION_ID5, ANSWER_OPTION_CAPTION5, ANSWER_OPTION_VALUE5);
        option3Q2 = createAnswerOption(ANSWER_OPTION_ID6, ANSWER_OPTION_CAPTION6, ANSWER_OPTION_VALUE6);
        optionImpact1Q1 = createAnswerOptionImpact(ANSWER_OPTION_IMPACT_ID1, ANSWER_OPTION_IMPACT_VALUE1);
        optionImpact2Q1 = createAnswerOptionImpact(ANSWER_OPTION_IMPACT_ID2, ANSWER_OPTION_IMPACT_VALUE2);
        optionImpact3Q1 = createAnswerOptionImpact(ANSWER_OPTION_IMPACT_ID3, ANSWER_OPTION_IMPACT_VALUE3);
        optionImpact1Q2 = createAnswerOptionImpact(ANSWER_OPTION_IMPACT_ID4, ANSWER_OPTION_IMPACT_VALUE4);
        optionImpact2Q2 = createAnswerOptionImpact(ANSWER_OPTION_IMPACT_ID5, ANSWER_OPTION_IMPACT_VALUE5);
        optionImpact3Q2 = createAnswerOptionImpact(ANSWER_OPTION_IMPACT_ID6, ANSWER_OPTION_IMPACT_VALUE6);
        color = createAssessmentColor();
        maturityLevel1 = createMaturityLevel(MATURITY_LEVEL_ID1, MATURITY_LEVEL_TITLE1, 1);
        maturityLevel2 = createMaturityLevel(MATURITY_LEVEL_ID2, MATURITY_LEVEL_TITLE2, 2);
        levelCompetence1 = createLevelCompetence(LEVEL_COMPETENCE_ID1, LEVEL_COMPETENCE_VALUE1);
        levelCompetence2 = createLevelCompetence(LEVEL_COMPETENCE_ID2, LEVEL_COMPETENCE_VALUE2);
        this.completeInitiation();
    }

    private void completeInitiation() {
        assessment.setColor(color);
        assessment.setMaturityLevel(maturityLevel2);
        assessment.setAssessmentKit(kit);

        result.setAssessment(assessment);

        subject.setAssessmentKit(kit);
        subject.getQuestionnaires().add(questionnaire);

        questionnaire.setAssessmentKit(kit);

        qualityAttribute.setAssessmentSubject(subject);

        qualityAttributeValue.setQualityAttribute(qualityAttribute);
        qualityAttributeValue.setAssessmentResult(result);

        question1.getQualityAttributes().add(qualityAttribute);
        question2.getQualityAttributes().add(qualityAttribute);

        questionImpact1.setQuestion(question1);
        questionImpact1.setMaturityLevel(maturityLevel1);
        questionImpact2.setQuestion(question2);
        questionImpact2.setMaturityLevel(maturityLevel2);

        answer1.setAssessmentResult(result);
        answer1.setQuestion(question1);
        answer1.setAnswerOption(option2Q1);

        option1Q1.setQuestion(question1);
        option2Q1.setQuestion(question1);
        option3Q1.setQuestion(question1);

        optionImpact1Q1.setOption(option1Q1);
        optionImpact1Q1.setImpact(questionImpact1);
        optionImpact2Q1.setOption(option2Q1);
        optionImpact2Q1.setImpact(questionImpact1);
        optionImpact3Q1.setOption(option3Q1);
        optionImpact3Q1.setImpact(questionImpact1);

        answer2.setAssessmentResult(result);
        answer2.setQuestion(question2);
        answer2.setAnswerOption(option1Q2);

        option1Q2.setQuestion(question2);
        option2Q2.setQuestion(question2);
        option3Q2.setQuestion(question2);

        optionImpact1Q2.setOption(option1Q2);
        optionImpact1Q2.setImpact(questionImpact2);
        optionImpact2Q2.setOption(option2Q2);
        optionImpact2Q2.setImpact(questionImpact2);
        optionImpact3Q2.setOption(option3Q2);
        optionImpact3Q2.setImpact(questionImpact2);

        maturityLevel1.setAssessmentKit(kit);
        maturityLevel2.setAssessmentKit(kit);

        levelCompetence1.setMaturityLevel(maturityLevel2);
        levelCompetence1.setMaturityLevelCompetence(maturityLevel1);
        levelCompetence2.setMaturityLevel(maturityLevel2);
        levelCompetence2.setMaturityLevelCompetence(maturityLevel2);
    }

}
