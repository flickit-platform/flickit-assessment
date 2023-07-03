package org.flickit.flickitassessmentcore;

import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.Constants.*;

@NoArgsConstructor
public class Utils {
    public static Assessment createAssessment() {
        return new Assessment(
            UUID.randomUUID(),
            ASSESSMENT_CODE,
            ASSESSMENT_TITLE,
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            AssessmentColor.BLUE.getId(),
            ASSESSMENT_SPACE_ID
        );
    }

    public static AssessmentKit createAssessmentKit() {
        return new AssessmentKit(
            KIT_ID_1,
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

    public static AssessmentResult createAssessmentResult() {
        return new AssessmentResult(
            UUID.randomUUID(),
            new Assessment(),
            false,
            null,
            new ArrayList<QualityAttributeValue>(),
            new ArrayList<AssessmentSubjectValue>()
        );
    }

    public static AssessmentSubject createAssessmentSubject() {
        return new AssessmentSubject(
            SUBJECT_ID,
            SUBJECT_CODE,
            SUBJECT_TITLE,
            SUBJECT_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1,
            new AssessmentKit(),
            new HashSet<Questionnaire>()
        );
    }

    public static AssessmentSubjectValue createAssessmentSubjectValue() {
        return new AssessmentSubjectValue(
            UUID.randomUUID(),
            new AssessmentSubject(),
            new MaturityLevel()
        );
    }

    public static Questionnaire createQuestionnaire() {
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

    public static QualityAttribute createQualityAttribute() {
        return new QualityAttribute(
            QUALITY_ATTRIBUTE_ID,
            QUALITY_ATTRIBUTE_CODE,
            QUALITY_ATTRIBUTE_TITLE,
            QUALITY_ATTRIBUTE_DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            new AssessmentSubject(),
            1,
            QUALITY_ATTRIBUTE_WEIGHT
        );
    }

    public static QualityAttributeValue createQualityAttributeValue() {
        return new QualityAttributeValue(
            UUID.randomUUID(),
            new QualityAttribute(),
            null // maturity level gonna be calculated
        );
    }

    public static Question createQuestion(Long id, String title, String desc) {
        return new Question(
            id,
            title,
            desc,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1,
            new Questionnaire(),
            new HashSet<QualityAttribute>()
        );
    }

    public static QuestionImpact createQuestionImpact(Long id, Integer level, Integer weight) {
        return new QuestionImpact(
            id,
            level,
            new MaturityLevel(),
            new Question(),
            new QualityAttribute(),
            weight
        );
    }

    public static Answer createAnswer() {
        return new Answer(
            UUID.randomUUID(),
            null,
            null,
            null
        );
    }

    public static AnswerOption createAnswerOption(Long id, String caption, Integer value) {
        return new AnswerOption(
            id,
            new Question(),
            caption,
            value,
            1
        );
    }

    public static AnswerOptionImpact createAnswerOptionImpact(Long id, BigDecimal value) {
        return new AnswerOptionImpact(
            id,
            value,
            null,
            null
        );
    }

    public static MaturityLevel createMaturityLevel(Long id, String title, int value) {
        return new MaturityLevel(
            id,
            title,
            value,
            new AssessmentKit()
        );
    }

    public static LevelCompetence createLevelCompetence(Long id, int value) {
        return new LevelCompetence(
            id,
            null,
            value,
            null
        );
    }

}
