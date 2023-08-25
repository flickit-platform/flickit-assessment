package org.flickit.flickitassessmentcore.domain;

import org.junit.jupiter.api.Test;

import static org.flickit.flickitassessmentcore.domain.Assessment.generateSlugCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentTest {

    @Test
    void generateSlugCode_NoWhitespace_ReturnsLowerCaseCode() {
        assertEquals("exampletitle",
            generateSlugCode("ExampleTitle"));

        assertEquals("with-whitespace",
            generateSlugCode("With Whitespace"));

        assertEquals("with-leading-and-trailing-whitespace",
            generateSlugCode("  With   Leading and Trailing   Whitespace  "));
    }

}
