package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_ADD;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_REMOVE;

@Slf4j
@Transactional(readOnly = true)
@Component
@RequiredArgsConstructor
public class SubjectUpdateValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();
        var codes = dslKit.getSubjects().stream().map(BaseDslModel::getCode).collect(Collectors.toSet());
        var savedCodes = savedKit.getSubjects().stream().map(Subject::getCode).collect(Collectors.toSet());
        var deletedCodes = savedCodes.stream().filter(s -> codes.stream().noneMatch(i -> i.equals(s))).toList();
        var newCodes = codes.stream().filter(i -> savedCodes.stream().noneMatch(s -> s.equals(i))).toList();

        if (!newCodes.isEmpty()) {
            notification.add(UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_ADD);
            log.debug("New subjects code in dsl for assessment kit id [{}] has found.", savedKit.getId());
        }
        if (!deletedCodes.isEmpty()) {
            notification.add(UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_REMOVE);
            log.debug("Old Subjects code in dsl for assessment kit id [{}] has not found.", savedKit.getId());
        }
        return notification;
    }
}
