package org.flickit.flickitassessmentcore.domain.calculate;

import org.flickit.flickitassessmentcore.domain.Question;
import org.flickit.flickitassessmentcore.domain.QuestionImpact;
import org.flickit.flickitassessmentcore.domain.calculate.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.QuestionMother;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void findImpactByMaturityLevel() {
        Question question = QuestionMother.withImpactsOnLevel24();

        QuestionImpact impact = question.findImpactByMaturityLevel(MaturityLevelMother.levelTwo());

        assertNotNull(impact);
        assertEquals(MaturityLevelMother.levelTwo().getId(), impact.getMaturityLevelId());
    }
}
