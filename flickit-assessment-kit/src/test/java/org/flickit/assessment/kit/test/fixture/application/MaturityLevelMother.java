package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;
import java.util.Map;

import static org.flickit.assessment.kit.test.fixture.application.LevelCompetenceMother.*;

public class MaturityLevelMother {

    private static final Map<Long, MaturityLevel> idToMaturityLevel = Map.of(
        LEVEL_ONE_ID, levelOne(),
        LEVEL_TWO_ID, levelTwo(),
        LEVEL_THREE_ID, levelThree(),
        LEVEL_FOUR_ID, levelFour(),
        LEVEL_FIVE_ID, levelFive()
    );

    public static String getCodeById(long id) {
        return getById(id).getCode();
    }

    public static MaturityLevel getById(long id) {
        return idToMaturityLevel.get(id);
    }

    public static List<MaturityLevel> fiveLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive());
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
            levelCompetenceForLevelTwo());
    }

    public static MaturityLevel levelThree() {
        return new MaturityLevel(
            Constants.LEVEL_THREE_ID,
            Constants.LEVEL_THREE_CODE,
            Constants.LEVEL_THREE_CODE,
            3,
            3,
            levelCompetenceForLevelThree());
    }

    public static MaturityLevel levelFour() {
        return new MaturityLevel(
            Constants.LEVEL_FOUR_ID,
            Constants.LEVEL_FOUR_CODE,
            Constants.LEVEL_FOUR_CODE,
            4,
            4,
            levelCompetenceForLevelFour());
    }

    public static MaturityLevel levelFive() {
        return new MaturityLevel(
            Constants.LEVEL_FIVE_ID,
            Constants.LEVEL_FIVE_CODE,
            Constants.LEVEL_FIVE_CODE,
            5,
            5,
            levelCompetenceForLevelFive());
    }
}
