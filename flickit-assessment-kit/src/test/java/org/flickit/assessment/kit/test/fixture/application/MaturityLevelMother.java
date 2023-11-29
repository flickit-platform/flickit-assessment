package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;

public class MaturityLevelMother {

    public static final Long LEVEL_ONE_ID = 10L;
    public static final String LEVEL_ONE_CODE = "Elementary";
    public static final Long LEVEL_TWO_ID = 20L;
    public static final String LEVEL_TWO_CODE = "Weak";
    public static final Long LEVEL_THREE_ID = 30L;
    public static final String LEVEL_THREE_CODE = "Moderate";
    public static final Long LEVEL_FOUR_ID = 40L;
    public static final String LEVEL_FOUR_CODE = "Good";
    public static final Long LEVEL_FIVE_ID = 50L;
    public static final String LEVEL_FIVE_CODE = "Great";
    public static final Long LEVEL_SIX_ID = 60L;
    public static final String LEVEL_SIX_CODE = "Awesome";

    public static List<MaturityLevel> fiveLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive(5));
    }

    public static MaturityLevel levelOne() {
        return new MaturityLevel(
            LEVEL_ONE_ID,
            LEVEL_ONE_CODE,
            LEVEL_ONE_CODE,
            1,
            1,
            List.of());
    }

    public static MaturityLevel levelTwo() {
        return new MaturityLevel(
            LEVEL_TWO_ID,
            LEVEL_TWO_CODE,
            LEVEL_TWO_CODE,
            2,
            2,
            LevelCompetenceMother.levelCompetenceForLevelTwo());
    }

    public static MaturityLevel levelThree() {
        return new MaturityLevel(
            LEVEL_THREE_ID,
            LEVEL_THREE_CODE,
            LEVEL_THREE_CODE,
            3,
            3,
            LevelCompetenceMother.levelCompetenceForLevelThree());
    }

    public static MaturityLevel levelFour() {
        return new MaturityLevel(
            LEVEL_FOUR_ID,
            LEVEL_FOUR_CODE,
            LEVEL_FOUR_CODE,
            4,
            4,
            LevelCompetenceMother.levelCompetenceForLevelFour());
    }

    public static MaturityLevel levelFive(int value) {
        return new MaturityLevel(
            LEVEL_FIVE_ID,
            LEVEL_FIVE_CODE,
            LEVEL_FIVE_CODE,
            5,
            value,
            LevelCompetenceMother.levelCompetenceForLevelFive());
    }
}
