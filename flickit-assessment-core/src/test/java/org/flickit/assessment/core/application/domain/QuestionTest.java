package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;

import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void testFindImpactByAttributeAndMaturityLevel_whenQuestionNotImpactsAttribute_thenReturnNull() {
        var otherAttributeId = 651L;
        var attributeId = 157L;
        var question = QuestionMother.withImpactsOnLevel24(otherAttributeId);

        var impact = question.findImpactByAttributeAndMaturityLevel(attributeId, levelTwo().getId());

        assertNull(impact);
    }

    @Test
    void testFindImpactByAttributeAndMaturityLevel_whenQuestionNotImpactsMaturityLevel_thenReturnNull() {
        var attributeId = 157L;
        var question = QuestionMother.withImpactsOnLevel24(attributeId);

        var impact = question.findImpactByAttributeAndMaturityLevel(attributeId, levelThree().getId());

        assertNull(impact);
    }

    @Test
    void testFindImpactByAttributeAndMaturityLevel_whenQuestionImpactsOnAttributeAndMaturityLevel_thenReturnMatchingImpact() {
        var attributeId = 157L;
        var question = QuestionMother.withImpactsOnLevel24(attributeId);

        var impact = question.findImpactByAttributeAndMaturityLevel(attributeId, levelTwo().getId());

        assertNotNull(impact);
        assertEquals(levelTwo().getId(), impact.getMaturityLevelId());
    }

    @Test
    void testGetAvgWeight_whenAttributeHasNoImpactOnQuestion_thenReturnZeroAvgWeight() {
        var otherAttributeId = 651L;
        var attributeId = 157L;
        var question = QuestionMother.withImpactsOnLevel24(otherAttributeId);

        double avgWeight = question.getAvgWeight(attributeId);
        assertEquals(0.0, avgWeight);
    }

    @Test
    void testGetAvgWeight_whenAttributeHasSingleImpactOnQuestion_thenReturnImpactWeight() {
        var attributeId = 156L;
        var question = QuestionMother.withImpactsOnLevel2(attributeId);

        double avgWeight = question.getAvgWeight(attributeId);
        assertEquals(question.getImpacts().getFirst().getWeight(), avgWeight);
    }

    @Test
    void testGetAvgWeight_whenAttributeHasMultipleImpactsOnQuestion_thenReturnAverageWeight() {
        var attributeId = 156L;
        var question = QuestionMother.withImpactsOnLevel23WithWeights(attributeId, 1, 2);

        double avgWeight = question.getAvgWeight(attributeId);
        assertEquals(1.5, avgWeight);
    }
}
