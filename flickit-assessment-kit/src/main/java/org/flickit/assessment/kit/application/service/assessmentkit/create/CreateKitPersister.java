package org.flickit.assessment.kit.application.service.assessmentkit.create;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

public interface CreateKitPersister {

    void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId);

    int order();

}
