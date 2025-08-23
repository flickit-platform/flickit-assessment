package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompositeUpdateKitValidator implements UpdateKitValidator {

    private final List<UpdateKitValidator> validators;

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification finalResult = new Notification();
        validators.forEach(v -> {
            Notification result = v.validate(savedKit, dslKit);
            finalResult.merge(result);
        });
        return finalResult;
    }
}
