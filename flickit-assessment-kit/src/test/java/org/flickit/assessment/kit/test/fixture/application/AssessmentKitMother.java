package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.Subject;

import java.time.LocalDateTime;
import java.util.List;

public class AssessmentKitMother {

    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String ABOUT = "about";
    public static final long EXPERT_GROUP_ID = 1L;
    private static long id = 134L;

public static AssessmentKit simpleKit() {
    return new AssessmentKit(
        id++,
        CODE + id,
        TITLE + id,
        SUMMARY,
        ABOUT,
        LocalDateTime.now(),
        LocalDateTime.now(),
        Boolean.TRUE,
        Boolean.FALSE,
        EXPERT_GROUP_ID,
        null,
        null,
        null);
}

    public static AssessmentKit completeKit(List<Subject> subjects, List<MaturityLevel> maturityLevels, List<Questionnaire> questionnaires) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            subjects,
            maturityLevels,
            questionnaires);
    }

    public static AssessmentKit kitWithMaturityLevels(List<MaturityLevel> maturityLevels) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            maturityLevels,
            null);
    }

    public static AssessmentKit kitWithQuestionnaires(List<Questionnaire> questionnaires) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            null,
            questionnaires
        );
    }

    public static AssessmentKit kitWithSubjects(List<Subject> subjects) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            subjects,
            null,
            null);
    }

    public static AssessmentKit kitWithSubjectsAndQuestionnaires(List<Subject> subjects, List<Questionnaire> questionnaires) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            subjects,
            null,
            questionnaires);
    }
}
