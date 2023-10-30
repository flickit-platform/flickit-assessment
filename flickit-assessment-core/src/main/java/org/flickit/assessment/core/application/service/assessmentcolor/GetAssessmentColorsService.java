package org.flickit.assessment.core.application.service.assessmentcolor;

import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentColorsUseCase;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetAssessmentColorsService implements GetAssessmentColorsUseCase {

    @Override
    public Result getAssessmentColors() {
        AssessmentColor defaultColor = AssessmentColor.getDefault();

        ColorItem defaultColorItem =
            new ColorItem(defaultColor.getId(), defaultColor.getTitle(), defaultColor.getCode());

        List<ColorItem> colorItems = Arrays.stream(AssessmentColor.values())
            .map(x -> new ColorItem(x.getId(), x.getTitle(), x.getCode()))
            .toList();

        return new Result(defaultColorItem, colorItems);
    }
}
