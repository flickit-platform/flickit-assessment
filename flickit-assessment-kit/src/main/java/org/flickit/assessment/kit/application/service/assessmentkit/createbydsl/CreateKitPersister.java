package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

import java.util.UUID;

public interface CreateKitPersister {

    void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitVersionId, UUID currentUserId);

    int order();

}
