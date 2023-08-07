package org.flickit.flickitassessmentcore.domain.calculate.mother;


import org.flickit.flickitassessmentcore.domain.calculate.LevelCompetence;

import static org.flickit.flickitassessmentcore.domain.calculate.mother.MaturityLevelMother.*;

public class LevelCompetenceMother {

    private static long id = 134L;

    public static LevelCompetence onLevelOne(int value) {
        return LevelCompetence.builder()
            .id(id++)
            .value(value)
            .maturityLevelId(LEVEL_ONE_ID)
            .build();
    }

    public static LevelCompetence onLevelTwo(int value) {
        return LevelCompetence.builder()
            .id(id++)
            .value(value)
            .maturityLevelId(MaturityLevelMother.LEVEL_TWO_ID)
            .build();
    }

    public static LevelCompetence onLevelThree(int value) {
        return LevelCompetence.builder()
            .id(id++)
            .value(value)
            .maturityLevelId(LEVEL_THREE_ID)
            .build();
    }

    public static LevelCompetence onLevelFour(int value) {
        return LevelCompetence.builder()
            .id(id++)
            .value(value)
            .maturityLevelId(LEVEL_FOUR_ID)
            .build();
    }

    public static LevelCompetence onLevelFive(int value) {
        return LevelCompetence.builder()
            .id(id++)
            .value(value)
            .maturityLevelId(LEVEL_FIVE_ID)
            .build();
    }
}
