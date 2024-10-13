package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.common.util.SlugCodeUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentTest {

    @Test
    void testGenerateSlugCode_NoWhitespace_ReturnsLowerCaseCode() {
        assertEquals("exampletitle",
            SlugCodeUtil.generateSlugCode("ExampleTitle"));

        assertEquals("with-whitespace",
            SlugCodeUtil.generateSlugCode("With Whitespace"));

        assertEquals("with-leading-and-trailing-whitespace",
            SlugCodeUtil.generateSlugCode("  With   Leading and Trailing   Whitespace  "));
    }

}
