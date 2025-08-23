package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.kit.application.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.flickit.assessment.common.application.domain.kit.translation.KitTranslation.MetadataTranslation;
import java.util.UUID;

public class AssessmentKitMother {

    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String ABOUT = "about";
    public static final String GOAL = "goal";
    public static final String CONTEXT = "context";
    public static final long EXPERT_GROUP_ID = 1L;
    private static long id = 134L;

    public static AssessmentKit simpleKit(){
        return simpleKitWithPrice(0);
    }

    public static AssessmentKit simpleKitWithPrice(long price) {
        return kitWithIsPrivateAndPrice(false, price);
    }

    public static AssessmentKit privateKitWithPrice(long price) {
        return kitWithIsPrivateAndPrice(true, price);
    }

    private static AssessmentKit kitWithIsPrivateAndPrice(boolean isPrivate, long price) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.TRUE,
            isPrivate,
            EXPERT_GROUP_ID,
            Map.of(KitLanguage.EN, new KitTranslation(TITLE, SUMMARY, ABOUT,
                new MetadataTranslation(GOAL, CONTEXT))),
            null,
            null,
            null,
            null,
            null,
            id++,
            price,
            null,
            null);
    }

    public static AssessmentKit notPublishedKit() {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.FALSE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            null,
            null,
            null,
            null,
            id++,
            0L);
    }

    public static AssessmentKit kitWithMetadata(KitMetadata metadata) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            Map.of(KitLanguage.EN, new KitTranslation(TITLE, SUMMARY, ABOUT,
                new MetadataTranslation(GOAL, CONTEXT))),
            null,
            null,
            null,
            null,
            null,
            id++,
            0L,
            metadata,
            null);
    }

    public static AssessmentKit completeKit(List<Subject> subjects,
                                            List<MaturityLevel> maturityLevels,
                                            List<Questionnaire> questionnaires,
                                            List<Measure> measures,
                                            List<AnswerRange> reusableAnswerRanges) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            subjects,
            maturityLevels,
            questionnaires,
            measures,
            reusableAnswerRanges,
            id++,
            0L);
    }

    public static AssessmentKit kitWithMaturityLevels(List<MaturityLevel> maturityLevels) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            maturityLevels,
            null,
            null,
            null,
            id++,
            0L);
    }

    public static AssessmentKit kitWithQuestionnaires(List<Questionnaire> questionnaires) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            null,
            questionnaires,
            null,
            null,
            id++,
            0L);
    }

    public static AssessmentKit kitWithMeasures(List<Measure> measures) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            null,
            null,
            measures,
            null,
            id++,
            0L);
    }

    public static AssessmentKit kitWithSubjects(List<Subject> subjects) {
        return kitWithSubjects(subjects, false);
    }

    public static AssessmentKit kitWithAnswerRanges(List<AnswerRange> answerRanges) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            null,
            null,
            null,
            answerRanges,
            id++,
            0L);
    }

    public static AssessmentKit kitWithSubjects(List<Subject> subjects, boolean isPrivate) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            isPrivate,
            EXPERT_GROUP_ID,
            subjects,
            null,
            null,
            null,
            null,
            id++,
            0L);
    }

    public static AssessmentKit kitWithSubjectsAndQuestionnaires(List<Subject> subjects, List<Questionnaire> questionnaires) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            subjects,
            null,
            questionnaires,
            null,
            null,
            id++,
            0L);
    }

    public static AssessmentKit privateKit() {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.FA,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            Boolean.TRUE,
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            null,
            null,
            null,
            null,
            id++,
            0L);
    }

    public static AssessmentKit kitWithKitVersionId(Long activeVersionId) {
        return new AssessmentKit(
            id++,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            KitLanguage.EN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            Boolean.FALSE,
            EXPERT_GROUP_ID,
            null,
            null,
            null,
            null,
            null,
            activeVersionId,
            0L);
    }
}
