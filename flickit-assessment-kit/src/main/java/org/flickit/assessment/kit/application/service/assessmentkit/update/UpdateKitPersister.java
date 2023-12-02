package org.flickit.assessment.kit.application.service.assessmentkit.update;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

public interface UpdateKitPersister {

    UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit);

    int order();
}
