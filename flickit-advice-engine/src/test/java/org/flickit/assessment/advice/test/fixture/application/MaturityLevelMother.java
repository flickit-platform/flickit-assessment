package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.MaturityLevel;

import java.util.List;

public class MaturityLevelMother {

    public static final long LEVEL_ONE_ID = 10;
    public static final long LEVEL_TWO_ID = 30;
    public static final long LEVEL_THREE_ID = 20;

    public static List<MaturityLevel> allLevels() {
        return List.of(levelOne(), levelTwo(), levelThree());
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
}
