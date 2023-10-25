package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.MaturityScore;

import java.util.Set;

import static org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother.*;

public class MaturityScoreMother {

    public static Set<MaturityScore> maturityScoresOnAllLevels() {
        return Set.of(new MaturityScore(levelOne().getId(), 80.0),
            new MaturityScore(levelTwo().getId(), null),
            new MaturityScore(levelThree().getId(), 10.23),
            new MaturityScore(levelFour().getId(), 25.0),
            new MaturityScore(levelFive().getId(), null));
    }
}
