package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.UpdateKitValidator;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class SubjectUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        var savedSubjectCodes = savedKit.getSubjects().stream().map(Subject::getCode).collect(toSet());
        var dslSubjectCodes = dslKit.getSubjects().stream().map(BaseDslModel::getCode).collect(toSet());

        var deletedCodes = savedSubjectCodes.stream().filter(s -> !dslSubjectCodes.contains(s)).collect(toSet());

        if (!deletedCodes.isEmpty())
            notification.add(new InvalidDeletionError(DslFieldNames.SUBJECT, deletedCodes));

        return notification;
    }
}
