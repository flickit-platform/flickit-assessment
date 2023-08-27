package org.flickit.flickitassessmentcore.application.domain.mother;


import org.flickit.flickitassessmentcore.application.domain.LevelCompetence;

import static org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother.*;

public class LevelCompetenceMother {

    private static long id = 134L;

    public static LevelCompetence onLevelOne(int value) {
        return new LevelCompetence(id++, value, LEVEL_ONE_ID);
    }

    public static LevelCompetence onLevelTwo(int value) {
        return new LevelCompetence(id++, value, LEVEL_TWO_ID);
    }

    public static LevelCompetence onLevelThree(int value) {
        return new LevelCompetence(id++, value, LEVEL_THREE_ID);
    }

    public static LevelCompetence onLevelFour(int value) {
        return new LevelCompetence(id++, value, LEVEL_FOUR_ID);
    }

    public static LevelCompetence onLevelFive(int value) {
        return new LevelCompetence(id++, value, LEVEL_FIVE_ID);
    }
}
