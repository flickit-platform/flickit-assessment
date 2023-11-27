package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AssessmentKit;

import java.time.LocalDateTime;

public class AssessmentKitMother {

    public static final String CODE = "code";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String ABOUT = "about";
    public static final long EXPERT_GROUP_ID = 1L;

    public static AssessmentKit kitWithFourLevels(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            MaturityLevelMother.fourLevels(),
            null);
    }

    public static AssessmentKit kitWithFiveLevels(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            MaturityLevelMother.fiveLevels(),
            null);
    }

    public static AssessmentKit kitWithFiveLevelsWithLevelFiveValue(Long id, int value) {
        return new AssessmentKit(id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            MaturityLevelMother.fiveLevelsWithLevelFiveValue(value),
            null);
    }
    public static AssessmentKit kitWithSixLevels(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            null,
            MaturityLevelMother.sixLevels(),
            null);
    }

    public static AssessmentKit kitWithTwoSubject(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            SubjectMother.twoSubject(),
            null,
            null);
    }

    public static AssessmentKit kitWithThreeSubject(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            SubjectMother.threeSubjects(),
            null,
            null);
    }

    public static AssessmentKit kitWithOneSubject(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            SubjectMother.oneSubjects(),
            null,
            null);
    }

    public static AssessmentKit kitWithTwoSubjectDiffCode(Long id) {
        return new AssessmentKit(
            id,
            CODE + id,
            TITLE + id,
            SUMMARY,
            ABOUT,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Boolean.TRUE,
            EXPERT_GROUP_ID,
            SubjectMother.twoSubjectDiffCode(),
            null,
            null);
    }
}
