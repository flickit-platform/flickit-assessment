package org.flickit.assessment.core.application.service.assessmentcolor;

import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentColorsUseCase.Result;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentColorsUseCase.ColorItem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetAssessmentColorsServiceTest {

    private final GetAssessmentColorsService service = new GetAssessmentColorsService();

    @Test
    void testGetAssessmentColors() {
        AssessmentColor defaultColor = AssessmentColor.getDefault();
        ColorItem defaultColorItem =
            new ColorItem(defaultColor.getId(), defaultColor.getTitle(), defaultColor.getCode());

        List<ColorItem> colorItems = Arrays.stream(AssessmentColor.values())
            .map(e -> new ColorItem(e.getId(), e.getTitle(), e.getCode()))
            .toList();

        assertEquals(new Result(defaultColorItem, colorItems), service.getAssessmentColors());
    }
}
