package org.flickit.flickitassessmentcore.application.service.assessmentcolor;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentColorsUseCase;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetAssessmentColorsService implements GetAssessmentColorsUseCase {

    @Override
    public AssessmentColors getAssessmentColors() {
        AssessmentColor defaultColor = AssessmentColor.getDefault();

        ColorItem defaultColorItem =
            new ColorItem(defaultColor.getId(), defaultColor.getTitle(), defaultColor.getCode());

        List<ColorItem> colorItems = Arrays.stream(AssessmentColor.values())
            .map(x -> new ColorItem(x.getId(), x.getTitle(), x.getCode()))
            .toList();

        return new AssessmentColors(defaultColorItem, colorItems);
    }
}
