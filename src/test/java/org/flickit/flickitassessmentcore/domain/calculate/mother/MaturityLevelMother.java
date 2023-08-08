package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.List;

import static org.flickit.flickitassessmentcore.domain.calculate.mother.LevelCompetenceMother.*;

public class MaturityLevelMother {

    public static final int LEVEL_ONE_ID = 10;
    public static final int LEVEL_TWO_ID = 20;
    public static final int LEVEL_THREE_ID = 30;
    public static final int LEVEL_FOUR_ID = 40;
    public static final int LEVEL_FIVE_ID = 50;

    public static List<MaturityLevel> allLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive());
    }

    public static MaturityLevel levelOne() {
        return new MaturityLevel(LEVEL_ONE_ID, 1,
            List.of());
    }

    public static MaturityLevel levelTwo() {
        return new MaturityLevel(LEVEL_TWO_ID, 2,
            List.of(onLevelTwo(60)));
    }

    public static MaturityLevel levelThree() {
        return new MaturityLevel(LEVEL_THREE_ID, 3,
            List.of(onLevelTwo(75),
                onLevelThree(60)));
    }

    public static MaturityLevel levelFour() {
        return new MaturityLevel(LEVEL_FOUR_ID, 4,
            List.of(onLevelTwo(85),
                onLevelThree(75),
                onLevelFour(60)));
    }

    public static MaturityLevel levelFive() {
        return new MaturityLevel(LEVEL_FIVE_ID, 5,
            List.of(onLevelTwo(95),
                onLevelThree(85),
                onLevelFour(70),
                onLevelFive(60)));
    }
}
