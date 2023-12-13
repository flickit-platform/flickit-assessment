package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.common.exception.api.Notification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.application.service.assessmentkit.validate.impl.DslFieldNames.ATTRIBUTE;

@Service
public class AttributeUpdateValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        var dslAttributeCodes = dslKit.getAttributes().stream()
            .map(BaseDslModel::getCode)
            .collect(Collectors.toSet());

        var savedAttributeCodes = savedKit.getSubjects().stream()
            .map(Subject::getAttributes)
            .flatMap(Collection::stream)
            .map(Attribute::getCode)
            .collect(Collectors.toSet());

        var deletedAttributeCodes = savedAttributeCodes.stream()
            .filter(e -> !dslAttributeCodes.contains(e))
            .collect(Collectors.toSet());

        var addedAttributeCodes = dslAttributeCodes.stream()
            .filter(e -> !savedAttributeCodes.contains(e))
            .collect(Collectors.toSet());

        if (!addedAttributeCodes.isEmpty())
            notification.add(new InvalidAdditionError(ATTRIBUTE, addedAttributeCodes));

        if (!deletedAttributeCodes.isEmpty())
            notification.add(new InvalidDeletionError(ATTRIBUTE, deletedAttributeCodes));

        return notification;
    }
}
