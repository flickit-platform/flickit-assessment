package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.Question;
import org.flickit.flickitassessmentcore.domain.calculate.QuestionImpact;

import java.util.List;

import static org.flickit.flickitassessmentcore.domain.calculate.mother.QuestionImpactMother.*;

public class QuestionMother {

    private static long id = 134L;

    public static Question withImpacts(List<QuestionImpact> impacts) {
        return new Question(id++, impacts);
    }

    public static Question withImpactsOnLevel23() {
        return new Question(id++, List.of(onLevelTwo(1), onLevelThree(1)));
    }

    public static Question withImpactsOnLevel24() {
        return new Question(id++, List.of(onLevelTwo(1), onLevelFour(1)));
    }

    public static Question withImpactsOnLevel34() {
        return new Question(id++, List.of(onLevelThree(1), onLevelFour(1)));
    }

    public static Question withImpactsOnLevel45() {
        return new Question(id++, List.of(onLevelFour(1), onLevelFive(1)));
    }

}
