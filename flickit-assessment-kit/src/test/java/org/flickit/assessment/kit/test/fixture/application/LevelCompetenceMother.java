package org.flickit.assessment.kit.test.fixture.application;

import java.util.HashMap;
import java.util.Map;

public class LevelCompetenceMother {

    public static Map<String, Integer> levelCompetenceForLevelTwo() {
        Map<String, Integer> levelCompetences = new HashMap<>();
        levelCompetences.put(MaturityLevelMother.LEVEL_TWO_CODE, 60);
        return levelCompetences;
    }

    public static Map<String, Integer> levelCompetenceForLevelThree() {
        Map<String, Integer> levelCompetences = new HashMap<>();
        levelCompetences.put(MaturityLevelMother.LEVEL_TWO_CODE, 75);
        levelCompetences.put(MaturityLevelMother.LEVEL_THREE_CODE, 60);
        return levelCompetences;
    }

    public static Map<String, Integer> levelCompetenceForLevelFour() {
        Map<String, Integer> levelCompetences = new HashMap<>();
        levelCompetences.put(MaturityLevelMother.LEVEL_TWO_CODE, 85);
        levelCompetences.put(MaturityLevelMother.LEVEL_THREE_CODE, 75);
        levelCompetences.put(MaturityLevelMother.LEVEL_FOUR_CODE, 60);
        return levelCompetences;
    }

    public static Map<String, Integer> levelCompetenceForLevelFive() {
        Map<String, Integer> levelCompetences = new HashMap<>();
        levelCompetences.put(MaturityLevelMother.LEVEL_TWO_CODE, 95);
        levelCompetences.put(MaturityLevelMother.LEVEL_THREE_CODE, 85);
        levelCompetences.put(MaturityLevelMother.LEVEL_FOUR_CODE, 75);
        levelCompetences.put(MaturityLevelMother.LEVEL_FIVE_CODE, 60);
        return levelCompetences;
    }


    public static Map<String, Integer> levelCompetenceForLevelSix() {
        Map<String, Integer> levelCompetences = new HashMap<>();
        levelCompetences.put(MaturityLevelMother.LEVEL_TWO_CODE, 95);
        levelCompetences.put(MaturityLevelMother.LEVEL_THREE_CODE, 85);
        levelCompetences.put(MaturityLevelMother.LEVEL_FOUR_CODE, 75);
        levelCompetences.put(MaturityLevelMother.LEVEL_FIVE_CODE, 70);
        levelCompetences.put(MaturityLevelMother.LEVEL_SIX_CODE, 60);
        return levelCompetences;
    }
}
