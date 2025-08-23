package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.UpdateKitValidator;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

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

        if (!deletedAttributeCodes.isEmpty())
            notification.add(new InvalidDeletionError(DslFieldNames.ATTRIBUTE, deletedAttributeCodes));

        return notification;
    }
}
