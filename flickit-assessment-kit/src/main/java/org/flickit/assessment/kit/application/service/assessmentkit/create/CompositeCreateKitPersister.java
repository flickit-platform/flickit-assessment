package org.flickit.assessment.kit.application.service.assessmentkit.create;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompositeCreateKitPersister {

    private final List<CreateKitPersister> persisters;

    public CompositeCreateKitPersister(List<CreateKitPersister> persisters) {
        this.persisters = persisters;
    }

    public CreateKitPersisterContext persist(AssessmentKitDslModel dslKit) {
        CreateKitPersisterContext ctx = new CreateKitPersisterContext();
        for (CreateKitPersister p : persisters) {
            p.persist(ctx, dslKit);
        }
        return ctx;
    }
}
