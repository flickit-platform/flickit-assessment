package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate.UpdateKitValidator;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class AnswerRangeUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        var savedRangesCodes = savedKit.getReusableAnswerRanges().stream().map(AnswerRange::getCode).collect(toSet());
        var dslRangesCodes = dslKit.getAnswerRanges().stream().map(BaseDslModel::getCode).collect(toSet());

        var deletedCodes = savedRangesCodes.stream().filter(s -> !dslRangesCodes.contains(s)).collect(toSet());

        if (!deletedCodes.isEmpty())
            notification.add(new InvalidDeletionError(DslFieldNames.ANSWER_RANGE, deletedCodes));

        return notification;
    }
}
