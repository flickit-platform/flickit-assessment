package org.flickit.assessment.kit.application.service.assessmentkit.validate;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.common.exception.api.Notification;

public interface UpdateKitValidator {

    Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit);
}
