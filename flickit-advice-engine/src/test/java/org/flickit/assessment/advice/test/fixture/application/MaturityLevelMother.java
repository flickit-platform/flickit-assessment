package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.MaturityLevel;

import java.util.List;

public class MaturityLevelMother {

    public static final long LEVEL_ONE_ID = 10;
    public static final long LEVEL_TWO_ID = 30;
    public static final long LEVEL_THREE_ID = 20;
    public static final long LEVEL_FOUR_ID = 50;
    public static final long LEVEL_FIVE_ID = 40;

    public static List<MaturityLevel> allLevels() {
        return List.of(levelOne(), levelTwo(), levelThree(), levelFour(), levelFive());
    }

    public static MaturityLevel levelOne() {
        return new MaturityLevel(LEVEL_ONE_ID, "one", 1);
    }

    public static MaturityLevel levelTwo() {
        return new MaturityLevel(LEVEL_TWO_ID, "two", 2);
    }

    public static MaturityLevel levelThree() {
        return new MaturityLevel(LEVEL_THREE_ID, "three", 3);
    }

    public static MaturityLevel levelFive() {
        return new MaturityLevel(LEVEL_FIVE_ID, "five", 5);
    }

    public static MaturityLevel levelFour() {
        return new MaturityLevel(LEVEL_FOUR_ID, "four", 4);
    }
}
