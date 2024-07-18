package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.AnswerMother;
import org.flickit.assessment.core.test.fixture.application.AnswerOptionImpactMother;
import org.flickit.assessment.core.test.fixture.application.AnswerOptionMother;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.junit.jupiter.api.Test;

import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;
import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    @Test
    void testFindImpactByMaturityLevel_optionOne() {
        Attribute attribute = AttributeMother.simpleAttribute();
        long attributeId = attribute.getId();
        AnswerOption optionOne = AnswerOptionMother.optionOne(attributeId);
        Answer answer = AnswerMother.answer(optionOne);

        AnswerOptionImpact onLevelOne = answer.findImpactByAttributeAndMaturityLevel(attribute, levelOne());
        AnswerOptionImpact onLevelTwo = answer.findImpactByAttributeAndMaturityLevel(attribute, levelTwo());
        AnswerOptionImpact onLevelThree = answer.findImpactByAttributeAndMaturityLevel(attribute, levelThree());
        AnswerOptionImpact onLevelFour = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFour());
        AnswerOptionImpact onLevelFive = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFive());

        assertNull(onLevelOne);
        assertNull(onLevelTwo);

        assertNotNull(onLevelThree);
        assertEquals(AnswerOptionImpactMother.onLevelThreeOfAttributeId(0.0, attributeId).getValue(), onLevelThree.getValue());

        assertNotNull(onLevelFour);
        assertEquals(AnswerOptionImpactMother.onLevelFourOfAttributeId(0.0, attributeId).getValue(), onLevelFour.getValue());

        assertNull(onLevelFive);
    }

    @Test
    void findImpactByMaturityLevel_optionTwo() {
        Attribute attribute = AttributeMother.simpleAttribute();
        long attributeId = attribute.getId();
        AnswerOption optionTwo = AnswerOptionMother.optionTwo(attributeId);
        Answer answer = AnswerMother.answer(optionTwo);

        AnswerOptionImpact onLevelOne = answer.findImpactByAttributeAndMaturityLevel(attribute, levelOne());
        AnswerOptionImpact onLevelTwo = answer.findImpactByAttributeAndMaturityLevel(attribute, levelTwo());
        AnswerOptionImpact onLevelThree = answer.findImpactByAttributeAndMaturityLevel(attribute, levelThree());
        AnswerOptionImpact onLevelFour = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFour());
        AnswerOptionImpact onLevelFive = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFive());

        assertNull(onLevelOne);
        assertNull(onLevelTwo);

        assertNotNull(onLevelThree);
        assertEquals(AnswerOptionImpactMother.onLevelThreeOfAttributeId(0.5, attributeId).getValue(), onLevelThree.getValue());

        assertNotNull(onLevelFour);
        assertEquals(AnswerOptionImpactMother.onLevelFourOfAttributeId(0.0, attributeId).getValue(), onLevelFour.getValue());

        assertNull(onLevelFive);
    }

    @Test
    void findImpactByMaturityLevel_optionThree() {
        Attribute attribute = AttributeMother.simpleAttribute();
        long attributeId = attribute.getId();
        AnswerOption optionThree = AnswerOptionMother.optionThree(attributeId);
        Answer answer = AnswerMother.answer(optionThree);

        AnswerOptionImpact onLevelOne = answer.findImpactByAttributeAndMaturityLevel(attribute, levelOne());
        AnswerOptionImpact onLevelTwo = answer.findImpactByAttributeAndMaturityLevel(attribute, levelTwo());
        AnswerOptionImpact onLevelThree = answer.findImpactByAttributeAndMaturityLevel(attribute, levelThree());
        AnswerOptionImpact onLevelFour = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFour());
        AnswerOptionImpact onLevelFive = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFive());

        assertNull(onLevelOne);
        assertNull(onLevelTwo);

        assertNotNull(onLevelThree);
        assertEquals(AnswerOptionImpactMother.onLevelThreeOfAttributeId(1.0, attributeId).getValue(), onLevelThree.getValue());

        assertNotNull(onLevelFour);
        assertEquals(AnswerOptionImpactMother.onLevelFourOfAttributeId(0.0, attributeId).getValue(), onLevelFour.getValue());

        assertNull(onLevelFive);
    }

    @Test
    void findImpactByMaturityLevel_optionFour() {
        Attribute attribute = AttributeMother.simpleAttribute();
        long attributeId = attribute.getId();
        AnswerOption optionFour = AnswerOptionMother.optionFour(attributeId);
        Answer answer = AnswerMother.answer(optionFour);

        AnswerOptionImpact onLevelOne = answer.findImpactByAttributeAndMaturityLevel(attribute, levelOne());
        AnswerOptionImpact onLevelTwo = answer.findImpactByAttributeAndMaturityLevel(attribute, levelTwo());
        AnswerOptionImpact onLevelThree = answer.findImpactByAttributeAndMaturityLevel(attribute, levelThree());
        AnswerOptionImpact onLevelFour = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFour());
        AnswerOptionImpact onLevelFive = answer.findImpactByAttributeAndMaturityLevel(attribute, levelFive());

        assertNull(onLevelOne);
        assertNull(onLevelTwo);

        assertNotNull(onLevelThree);
        assertEquals(AnswerOptionImpactMother.onLevelThreeOfAttributeId(1.0, attributeId).getValue(), onLevelThree.getValue());

        assertNotNull(onLevelFour);
        assertEquals(AnswerOptionImpactMother.onLevelFourOfAttributeId(1.0, attributeId).getValue(), onLevelFour.getValue());

        assertNull(onLevelFive);
    }
}
