package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.MaturityScore;

import java.util.Set;

import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;

public class MaturityScoreMother {

    public static Set<MaturityScore> maturityScoresOnAllLevels() {
        return Set.of(new MaturityScore(levelOne().getId(), 80.0),
            new MaturityScore(levelTwo().getId(), null),
            new MaturityScore(levelThree().getId(), 10.23),
            new MaturityScore(levelFour().getId(), 25.0),
            new MaturityScore(levelFive().getId(), null));
    }
}
