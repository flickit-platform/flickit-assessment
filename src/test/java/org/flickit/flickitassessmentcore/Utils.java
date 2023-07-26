package org.flickit.flickitassessmentcore;

import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        return new AssessmentKit(KIT_ID_1);
    }

    public static AssessmentResult createAssessmentResult() {
        return new AssessmentResult(
            UUID.randomUUID(),
            new Assessment(),
            false,
            null
        );
    }

    public static Subject createSubject() {
        return new Subject(SUBJECT_ID);
    }

    public static SubjectValue createSubjectValue() {
        return new SubjectValue(
            UUID.randomUUID(),
            new Subject(),
            new MaturityLevel(),
            null
        );
    }

    public static QualityAttribute createQualityAttribute() {
        return new QualityAttribute(
            QUALITY_ATTRIBUTE_ID,
            QUALITY_ATTRIBUTE_WEIGHT
        );
    }

    public static QualityAttributeValue createQualityAttributeValue() {
        return new QualityAttributeValue(
            UUID.randomUUID(),
            new QualityAttribute(),
            null, // maturity level gonna be calculated
            null
        );
    }

    public static Question createQuestion(Long id, String title) {
        return new Question(
            id,
            title,
            new ArrayList<QuestionImpact>()
        );
    }

    public static QuestionImpact createQuestionImpact(Long id, Long maturityLevelId, Long qualityAttributeId, Double weight) {
        return new QuestionImpact(
            id,
            maturityLevelId,
            qualityAttributeId,
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
            caption,
            value,
            1
        );
    }

    public static AnswerOptionImpact createAnswerOptionImpact(Long id, Double value) {
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
            new ArrayList<>()
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
