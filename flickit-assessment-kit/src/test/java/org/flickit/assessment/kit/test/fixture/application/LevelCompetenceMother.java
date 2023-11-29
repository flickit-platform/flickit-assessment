package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;

import java.util.ArrayList;
import java.util.List;

public class LevelCompetenceMother {

    public static List<MaturityLevelCompetence> levelCompetenceForLevelTwo() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_TWO_ID, Constants.LEVEL_TWO_CODE, 60));
        return levelCompetences;
    }

    public static List<MaturityLevelCompetence> levelCompetenceForLevelThree() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_TWO_ID, Constants.LEVEL_TWO_CODE, 75));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_THREE_ID, Constants.LEVEL_THREE_CODE, 60));
        return levelCompetences;
    }

    public static List<MaturityLevelCompetence> levelCompetenceForLevelFour() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_TWO_ID, Constants.LEVEL_TWO_CODE, 85));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_THREE_ID, Constants.LEVEL_THREE_CODE, 75));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_FOUR_ID, Constants.LEVEL_FOUR_CODE, 60));
        return levelCompetences;
    }

    public static List<MaturityLevelCompetence> levelCompetenceForLevelFive() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_TWO_ID, Constants.LEVEL_TWO_CODE, 95));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_THREE_ID, Constants.LEVEL_THREE_CODE, 85));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_FOUR_ID, Constants.LEVEL_FOUR_CODE, 75));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_FIVE_ID, Constants.LEVEL_FIVE_CODE, 60));
        return levelCompetences;
    }


    public static List<MaturityLevelCompetence> levelCompetenceForLevelSix() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_TWO_ID, Constants.LEVEL_TWO_CODE, 95));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_THREE_ID, Constants.LEVEL_THREE_CODE, 85));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_FOUR_ID, Constants.LEVEL_FOUR_CODE, 75));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_FIVE_ID, Constants.LEVEL_FIVE_CODE, 70));
        levelCompetences.add(new MaturityLevelCompetence(Constants.LEVEL_SIX_ID, Constants.LEVEL_SIX_CODE, 60));
        return levelCompetences;
    }
}
