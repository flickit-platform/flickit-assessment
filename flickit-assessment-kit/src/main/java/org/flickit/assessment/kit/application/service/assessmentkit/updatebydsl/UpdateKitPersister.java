package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

import java.util.UUID;

public interface UpdateKitPersister {

    UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                     AssessmentKit savedKit,
                                     AssessmentKitDslModel dslKit,
                                     UUID currentUserId);

    int order();
}
