package org.flickit.assessment.kit.application.service.assessmentkit.create;

import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompositeCreateKitPersister {

    private final List<CreateKitPersister> persisters;

    public CompositeCreateKitPersister(List<CreateKitPersister> persisters) {
        this.persisters = persisters;
        checkDuplicateOrders(this.persisters);
    }

    private void checkDuplicateOrders(List<CreateKitPersister> persisters) {
        Set<Integer> orderSet = persisters.stream().map(CreateKitPersister::order).collect(Collectors.toSet());
        if (orderSet.size() != persisters.size())
            throw new IllegalStateException();
    }

    public CreateKitPersisterContext persist(AssessmentKitDslModel dslKit, Long kitId) {
        CreateKitPersisterContext ctx = new CreateKitPersisterContext();
        for (CreateKitPersister p : persisters) {
            p.persist(ctx, dslKit, kitId);
        }
        return ctx;
    }
}
