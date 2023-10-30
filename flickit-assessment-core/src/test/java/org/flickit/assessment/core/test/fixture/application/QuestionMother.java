package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.QuestionImpact;

import java.util.List;

public class QuestionMother {

    private static long id = 134L;

    public static Question withImpacts(List<QuestionImpact> impacts) {
        return new Question(id++, impacts);
    }

    public static Question withImpactsOnLevel23() {
        return new Question(id++, List.of(QuestionImpactMother.onLevelTwo(1), QuestionImpactMother.onLevelThree(1)));
    }

    public static Question withImpactsOnLevel24() {
        return new Question(id++, List.of(QuestionImpactMother.onLevelTwo(1), QuestionImpactMother.onLevelFour(1)));
    }

    public static Question withImpactsOnLevel34() {
        return new Question(id++, List.of(QuestionImpactMother.onLevelThree(1), QuestionImpactMother.onLevelFour(1)));
    }

    public static Question withImpactsOnLevel45() {
        return new Question(id++, List.of(QuestionImpactMother.onLevelFour(1), QuestionImpactMother.onLevelFive(1)));
    }

    public static Question withNoImpact() {
        return new Question(id++, null);
    }

}
