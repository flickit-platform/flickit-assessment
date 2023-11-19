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

    public static List<MaturityLevel> fourLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour());
    }

    public static List<MaturityLevel> fiveLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive());
    }

    public static List<MaturityLevel> sixLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive(), levelSix());
    }

    public static MaturityLevel levelOne() {
        return new MaturityLevel(LEVEL_ONE_ID,
            LEVEL_ONE_CODE,
            LEVEL_ONE_CODE,
            null,
            1,
            null,
            1);
    }

    public static MaturityLevel levelTwo() {
        return new MaturityLevel(LEVEL_TWO_ID,
            LEVEL_TWO_CODE,
            LEVEL_TWO_CODE,
            null,
            2,
            LevelCompetenceMother.levelCompetenceForLevelTwo(),
            2);
    }

    public static MaturityLevel levelThree() {
        return new MaturityLevel(LEVEL_THREE_ID,
            LEVEL_THREE_CODE,
            LEVEL_THREE_CODE,
            null,
            3,
            LevelCompetenceMother.levelCompetenceForLevelThree(),
            3);
    }

    public static MaturityLevel levelFour() {
        return new MaturityLevel(LEVEL_FOUR_ID,
            LEVEL_FOUR_CODE,
            LEVEL_FOUR_CODE,
            null,
            4,
            LevelCompetenceMother.levelCompetenceForLevelFour(),
            4);
    }

    public static MaturityLevel levelFive() {
        return new MaturityLevel(
            LEVEL_FIVE_ID,
            LEVEL_FIVE_CODE,
            LEVEL_FIVE_CODE,
            null,
            5,
            LevelCompetenceMother.levelCompetenceForLevelFive(),
            5);
    }

    public static MaturityLevel levelSix() {
        return new MaturityLevel(
            LEVEL_SIX_ID,
            LEVEL_SIX_CODE,
            LEVEL_SIX_CODE,
            null,
            6,
            LevelCompetenceMother.levelCompetenceForLevelSix(),
            6);
    }

}
