package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.MaturityLevelCompetence;

import java.util.ArrayList;
import java.util.List;

import static org.flickit.assessment.kit.test.fixture.application.Constants.*;

public class LevelCompetenceMother {

    private static long id = 5613;

    public static List<MaturityLevelCompetence> levelCompetenceForLevelTwo() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_TWO_ID, 60));
        return levelCompetences;
    }

    public static List<MaturityLevelCompetence> levelCompetenceForLevelThree() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_TWO_ID, 75));
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_THREE_ID, 60));
        return levelCompetences;
    }

    public static List<MaturityLevelCompetence> levelCompetenceForLevelFour() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_TWO_ID, 85));
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_THREE_ID, 75));
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_FOUR_ID, 60));
        return levelCompetences;
    }

    public static List<MaturityLevelCompetence> levelCompetenceForLevelFive() {
        List<MaturityLevelCompetence> levelCompetences = new ArrayList<>();
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_TWO_ID, 95));
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_THREE_ID, 85));
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_FOUR_ID, 75));
        levelCompetences.add(new MaturityLevelCompetence(id++, LEVEL_FIVE_ID, 60));
        return levelCompetences;
    }
}
