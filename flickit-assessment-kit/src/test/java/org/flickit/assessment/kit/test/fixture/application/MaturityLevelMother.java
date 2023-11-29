package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;

public class MaturityLevelMother {

    public static List<MaturityLevel> fiveLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive(5));
    }

    public static MaturityLevel levelOne() {
        return new MaturityLevel(
            Constants.LEVEL_ONE_ID,
            Constants.LEVEL_ONE_CODE,
            Constants.LEVEL_ONE_CODE,
            1,
            1,
            List.of());
    }

    public static MaturityLevel levelTwo() {
        return new MaturityLevel(
            Constants.LEVEL_TWO_ID,
            Constants.LEVEL_TWO_CODE,
            Constants.LEVEL_TWO_CODE,
            2,
            2,
            LevelCompetenceMother.levelCompetenceForLevelTwo());
    }

    public static MaturityLevel levelThree() {
        return new MaturityLevel(
            Constants.LEVEL_THREE_ID,
            Constants.LEVEL_THREE_CODE,
            Constants.LEVEL_THREE_CODE,
            3,
            3,
            LevelCompetenceMother.levelCompetenceForLevelThree());
    }

    public static MaturityLevel levelFour() {
        return new MaturityLevel(
            Constants.LEVEL_FOUR_ID,
            Constants.LEVEL_FOUR_CODE,
            Constants.LEVEL_FOUR_CODE,
            4,
            4,
            LevelCompetenceMother.levelCompetenceForLevelFour());
    }

    public static MaturityLevel levelFive(int value) {
        return new MaturityLevel(
            Constants.LEVEL_FIVE_ID,
            Constants.LEVEL_FIVE_CODE,
            Constants.LEVEL_FIVE_CODE,
            5,
            value,
            LevelCompetenceMother.levelCompetenceForLevelFive());
    }
}
