package org.flickit.flickitassessmentcore;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.flickit.flickitassessmentcore.domain.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.Constants.*;

@NoArgsConstructor
public class Utils {
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
            QUALITY_ATTRIBUTE_WEIGHT,
            new HashSet<QualityAttributeValue>(),
            question.getQuestionImpacts(),
            new HashSet<Question>(List.of(new Question[]{question}))
        );
        subject.getQualityAttributes().add(qualityAttribute);
        question.getQualityAttributes().add(qualityAttribute);
        return qualityAttribute;
    }

    public AssessmentSubject createAssessmentSubject() {
        Questionnaire questionnaire = createQuestionnaire();
        AssessmentSubject subject = new AssessmentSubject(
            SUBJECT_ID,
            SUBJECT_CODE,
            SUBJECT_TITLE,
            SUBJECT_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1,
            createAssessmentKit(KIT_ID_1),
            new HashSet<Questionnaire>(List.of(new Questionnaire[]{questionnaire})),
            new HashSet<QualityAttribute>()
        );
        questionnaire.getSubjects().add(subject);
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
            new AssessmentKit(),
            new HashSet<Question>(),
            new HashSet<AssessmentSubject>()
        );
    }

    public Question createQuestion() {
        return new Question(
            QUESTION_ID,
            QUESTION_TITLE,
            QUESTION_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1,
            new Questionnaire(),
            new HashSet<Answer>(),
            new HashSet<QuestionImpact>(),
            new HashSet<AnswerOption>(),
            new HashSet<Evidence>(),
            new HashSet<QualityAttribute>()
        );
    }

    public AssessmentKit createAssessmentKit(Long id) {
        AssessmentSubject subject = createAssessmentSubject();
        Questionnaire questionnaire = createQuestionnaire();
        MaturityLevel maturityLevel1 = createMaturityLevel();
        AssessmentKit assessmentKit = new AssessmentKit(
            id,
            KIT_CODE,
            KIT_TITLE,
            KIT_SUMMARY,
            KIT_ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            true,
            new HashSet<MaturityLevel>(List.of(new MaturityLevel[]{maturityLevel1})),
            new HashSet<AssessmentSubject>(List.of(new AssessmentSubject[]{subject})),
            new HashSet<Assessment>(),
            new HashSet<Questionnaire>(List.of(new Questionnaire[]{questionnaire}))
        );
        subject.setAssessmentKit(assessmentKit);
        questionnaire.setAssessmentKit(assessmentKit);
        questionnaire.getSubjects().add(subject);
        subject.getQuestionnaires().add(questionnaire);
        return assessmentKit;
    }

    public AssessmentResult createAssessmentResult() {
        Assessment assessment = createAssessment();
        QualityAttributeValue qualityAttributeValue = createQualityAttributeValue();
        Answer answer = createAnswer();
        AssessmentResult assessmentResult = new AssessmentResult(
            UUID.randomUUID(),
            assessment,
            List.of(new Answer[]{answer}),
            List.of(new QualityAttributeValue[]{qualityAttributeValue})
        );
        assessment.getAssessmentResults().add(assessmentResult);
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
        QualityAttributeValue qualityAttributeValue = new QualityAttributeValue(
            UUID.randomUUID(),
            new AssessmentResult(),
            qualityAttribute,
            null
        );
        qualityAttribute.getQualityAttributeValues().add(qualityAttributeValue);
        return qualityAttributeValue;
    }

    public Assessment createAssessment() {
        AssessmentColor assessmentColor = createAssessmentColor();
        Assessment assessment = new Assessment(
            UUID.randomUUID(),
            ASSESSMENT_CODE,
            ASSESSMENT_TITLE,
            ASSESSMENT_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            createAssessmentKit(KIT_ID_1),
            assessmentColor,
            ASSESSMENT_SPACE_ID,
            createMaturityLevel(),
            new HashSet<>(),
            null
        );
        assessmentColor.getAssessments().add(assessment);
        return assessment;
    }

    public AssessmentColor createAssessmentColor() {
        return new AssessmentColor(
            COLOR_ID,
            COLOR_TITLE,
            COLOR_CODE,
            new HashSet<Assessment>()
        );
    }

    public MaturityLevel createMaturityLevel() {
        return new MaturityLevel(
            MATURITY_LEVEL_ID,
            MATURITY_LEVEL_TITLE,
            1,
            new AssessmentKit(),
            new HashSet<Assessment>(),
            new HashSet<QualityAttributeValue>(),
            new HashSet<QuestionImpact>(),
            new HashSet<LevelCompetence>()
        );
    }
}
