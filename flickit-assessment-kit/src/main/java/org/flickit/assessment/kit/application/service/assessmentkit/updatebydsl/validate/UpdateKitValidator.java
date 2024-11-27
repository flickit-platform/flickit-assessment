package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.validate;

import org.flickit.assessment.common.exception.api.Notification;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

public interface UpdateKitValidator {

    Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit);
}
