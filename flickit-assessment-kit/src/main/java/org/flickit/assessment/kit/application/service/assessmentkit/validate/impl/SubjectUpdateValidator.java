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

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

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
        if (!savedCodes.equals(codes)) {
            if (savedCodes.size() > codes.size()) {
                notification.add(UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_REMOVE);
            } else if (savedCodes.size() < codes.size()) {
                notification.add(UPDATE_SUBJECT_BY_DSL_SUBJECT_NOT_ADD);
            } else {
                notification.add(UPDATE_SUBJECT_BY_DSL_SUBJECT_CODE_NOT_CHANGE);
            }
            log.debug("Subjects code for assessment kit id [{}] has changes.", savedKit.getId());
        }
        return notification;
    }
}
