package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentTest {

    @Test
    void testGenerateSlugCode_NoWhitespace_ReturnsLowerCaseCode() {
        assertEquals("exampletitle",
            generateSlugCode("ExampleTitle"));

        assertEquals("with-whitespace",
            generateSlugCode("With Whitespace"));

        assertEquals("with-leading-and-trailing-whitespace",
            generateSlugCode("  With   Leading and Trailing   Whitespace  "));
    }

}
