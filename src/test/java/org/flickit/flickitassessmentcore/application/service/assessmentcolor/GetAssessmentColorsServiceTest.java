package org.flickit.flickitassessmentcore.application.service.assessmentcolor;

import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase.Result;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase.ColorItem;
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
