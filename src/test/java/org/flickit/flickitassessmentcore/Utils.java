package org.flickit.flickitassessmentcore;

import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.domain.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.Constants.*;

@NoArgsConstructor
public class Utils {
    public static void completeInitiation(AssessmentResult assessmentResult, AssessmentKit assessmentKit) {
        assessmentResult.getAssessment().getMaturityLevel().setAssessmentKit(assessmentKit);
    }

    // TODO: this class is in development
    public QualityAttribute createQualityAttribute() {
        AssessmentSubject subject = createAssessmentSubject();
        Question question = createQuestion();
        QualityAttribute qualityAttribute = new QualityAttribute(
            QUALITY_ATTRIBUTE_ID,
            QUALITY_ATTRIBUTE_CODE,
            QUALITY_ATTRIBUTE_TITLE,
            QUALITY_ATTRIBUTE_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            subject,
            1,
            QUALITY_ATTRIBUTE_WEIGHT
        );
        question.getQualityAttributes().add(qualityAttribute);
        return qualityAttribute;
    }

    public AssessmentSubject createAssessmentSubject() {
        Questionnaire questionnaire = createQuestionnaire();
        AssessmentKit assessmentKit = createAssessmentKit(KIT_ID_1);
        AssessmentSubject subject = new AssessmentSubject(
            SUBJECT_ID,
            SUBJECT_CODE,
            SUBJECT_TITLE,
            SUBJECT_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1,
            assessmentKit,
            new HashSet<Questionnaire>(List.of(questionnaire))
        );
        questionnaire.setAssessmentKit(assessmentKit);
        return subject;
    }

    private Questionnaire createQuestionnaire() {
        return new Questionnaire(
            QUESTIONNAIRE_ID,
            QUESTIONNAIRE_CODE,
            QUESTIONNAIRE_TITLE,
            QUESTIONNAIRE_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1,
            new AssessmentKit()
        );
    }

    public Question createQuestion() {
        QuestionImpact questionImpact = createQuestionImpact();
        AnswerOption answerOption = createAnswerOption();
        Answer answer = createAnswer();
        Question question = new Question(
            QUESTION_ID,
            QUESTION_TITLE,
            QUESTION_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1,
            new Questionnaire(),
            new HashSet<QualityAttribute>()
        );
        answerOption.setQuestion(question);
        answer.setQuestion(question);
        answer.setAnswerOption(answerOption);
        questionImpact.setQuestion(question);
        return question;
    }

    private QuestionImpact createQuestionImpact() {
        return new QuestionImpact(
            QUESTION_IMPACT_ID,
            QUESTION_IMPACT_LEVEL,
            new MaturityLevel(),
            new Question(),
            new QualityAttribute(),
            QUESTION_IMPACT_WEIGHT
        );
    }

    private AnswerOption createAnswerOption() {
        AnswerOptionImpact answerOptionImpact = createAnswerOptionImpact();
        AnswerOption answerOption = new AnswerOption(
            ANSWER_OPTION_ID,
            new Question(),
            ANSWER_OPTION_CAPTION,
            ANSWER_OPTION_VALUE,
            1
        );
        answerOptionImpact.setOption(answerOption);
        return answerOption;
    }

    private AnswerOptionImpact createAnswerOptionImpact() {
        return new AnswerOptionImpact(
            ANSWER_OPTION_IMPACT_ID,
            ANSWER_OPTION_IMPACT_VALUE,
            new AnswerOption(),
            new QuestionImpact()
        );
    }

    public AssessmentKit createAssessmentKit(Long id) {
        return new AssessmentKit(
            id,
            KIT_CODE,
            KIT_TITLE,
            KIT_SUMMARY,
            KIT_ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            true
        );
    }

    public AssessmentResult createAssessmentResult() {
        Assessment assessment = createAssessment();
        QualityAttributeValue qualityAttributeValue = createQualityAttributeValue();
        Answer answer = createAnswer();
        AssessmentResult assessmentResult = new AssessmentResult(
            UUID.randomUUID(),
            assessment
        );
        answer.setAssessmentResult(assessmentResult);
        qualityAttributeValue.setAssessmentResult(assessmentResult);
        return assessmentResult;
    }

    public Answer createAnswer() {
        return new Answer(
            UUID.randomUUID(),
            new AssessmentResult(),
            new Question(),
            new AnswerOption()
        );
    }

    public QualityAttributeValue createQualityAttributeValue() {
        QualityAttribute qualityAttribute = createQualityAttribute();
        return new QualityAttributeValue(
            UUID.randomUUID(),
            new AssessmentResult(),
            qualityAttribute,
            null // maturity level gonna be calculated
        );
    }

    public Assessment createAssessment() {
        AssessmentColor assessmentColor = createAssessmentColor();
        MaturityLevel maturityLevel1 = createMaturityLevel(MATURITY_LEVEL_ID1, MATURITY_LEVEL_TITLE1, 1);
        MaturityLevel maturityLevel2 = createMaturityLevel(MATURITY_LEVEL_ID2, MATURITY_LEVEL_TITLE2, 2);
        LevelCompetence levelCompetence1 = createLevelCompetence(LEVEL_COMPETENCE_ID1, LEVEL_COMPETENCE_VALUE1);
        LevelCompetence levelCompetence2 = createLevelCompetence(LEVEL_COMPETENCE_ID2, LEVEL_COMPETENCE_VALUE2);
        levelCompetence1.setMaturityLevel(maturityLevel2);
        levelCompetence1.setMaturityLevelCompetence(maturityLevel1);
        levelCompetence2.setMaturityLevel(maturityLevel2);
        levelCompetence2.setMaturityLevelCompetence(maturityLevel2);
        return new Assessment(
            UUID.randomUUID(),
            ASSESSMENT_CODE,
            ASSESSMENT_TITLE,
            ASSESSMENT_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            createAssessmentKit(KIT_ID_1),
            assessmentColor,
            ASSESSMENT_SPACE_ID,
            maturityLevel2 // It must be set after ml calc of qa and subject, but we initiate for now
        );
    }

    public AssessmentColor createAssessmentColor() {
        return new AssessmentColor(
            COLOR_ID,
            COLOR_TITLE,
            COLOR_CODE
        );
    }

    public MaturityLevel createMaturityLevel(Long id, String title, int value) {
        return new MaturityLevel(
            id,
            title,
            value,
            new AssessmentKit()
        );
    }

    private LevelCompetence createLevelCompetence(Long id, int value) {
        return new LevelCompetence(
            id,
            new MaturityLevel(),
            value,
            new MaturityLevel()
        );
    }

}
