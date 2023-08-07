package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.MaturityLevel;

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

    public static MaturityLevel withLevel(int level) {
        return MaturityLevel.builder()
            .level(level)
            .build();
    }

    public static MaturityLevel levelOne() {
        return MaturityLevel.builder()
            .id(LEVEL_ONE_ID)
            .level(1)
            .levelCompetences(List.of())
            .build();
    }

    public static MaturityLevel levelTwo() {
        return MaturityLevel.builder()
            .id(LEVEL_TWO_ID)
            .level(2)
            .levelCompetences(List.of(onLevelTwo(60)))
            .build();
    }

    public static MaturityLevel levelThree() {
        return MaturityLevel.builder()
            .id(LEVEL_THREE_ID)
            .level(3)
            .levelCompetences(List.of(onLevelTwo(75), onLevelThree(60)))
            .build();
    }

    public static MaturityLevel levelFour() {
        return MaturityLevel.builder()
            .id(LEVEL_FOUR_ID)
            .level(4)
            .levelCompetences(List.of(onLevelTwo(85), onLevelThree(75), onLevelFour(60)))
            .build();
    }

    public static MaturityLevel levelFive() {
        return MaturityLevel.builder()
            .id(LEVEL_FIVE_ID)
            .level(5)
            .levelCompetences(List.of(onLevelTwo(95), onLevelThree(85), onLevelFour(70), onLevelFive(60)))
            .build();
    }
}
