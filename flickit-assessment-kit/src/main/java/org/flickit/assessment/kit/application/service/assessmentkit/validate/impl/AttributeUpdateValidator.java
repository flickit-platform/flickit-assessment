package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_ATTRIBUTE_BY_DSL_ATTRIBUTE_DELETION_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_ATTRIBUTE_BY_DSL_ATTRIBUTE_ADDITION_NOT_ALLOWED;

@Service
public class AttributeUpdateValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        var dslKitAttributeCodes = dslKit.getAttributes().stream()
            .map(BaseDslModel::getCode)
            .collect(Collectors.toSet());

        var currentAttributeCodes = savedKit.getSubjects().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .map(Attribute::getCode)
            .toList();

        var deletedAttributeCodes = currentAttributeCodes.stream()
            .filter(e -> dslKitAttributeCodes.stream().noneMatch(o -> o.equals(e)))
            .toList();

        var addedAttributeCodes = dslKitAttributeCodes.stream()
            .filter(e -> currentAttributeCodes.stream().noneMatch(o -> o.equals(e)))
            .toList();

        if (!addedAttributeCodes.isEmpty())
            notification.add(UPDATE_ATTRIBUTE_BY_DSL_ATTRIBUTE_ADDITION_NOT_ALLOWED);

        if (!deletedAttributeCodes.isEmpty())
            notification.add(UPDATE_ATTRIBUTE_BY_DSL_ATTRIBUTE_DELETION_NOT_ALLOWED);

        return notification;
    }
}
