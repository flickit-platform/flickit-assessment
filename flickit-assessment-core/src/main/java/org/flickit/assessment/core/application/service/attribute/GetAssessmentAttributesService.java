package org.flickit.assessment.core.application.service.attribute;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentAttributesService implements GetAssessmentAttributesUseCase {

    @Override
    public Result getAssessmentAttributes(Param param) {
        return null;
    }
}
