package org.flickit.flickitassessmentcore.application.service.assessmentcolor;

import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GetAssessmentColorsServiceTest {

    private final GetAssessmentColorsService service = new GetAssessmentColorsService();

    @Test
    void testGetAssessmentColors() {
        assertEquals(Arrays.asList(AssessmentColor.values()), service.getAssessmentColors());
    }
}
