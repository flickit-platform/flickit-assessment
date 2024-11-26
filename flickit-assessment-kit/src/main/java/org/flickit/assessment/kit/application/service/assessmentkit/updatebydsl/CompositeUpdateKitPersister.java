package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

@Service
public class CompositeUpdateKitPersister {

    private final List<UpdateKitPersister> persisters;

    public CompositeUpdateKitPersister(List<UpdateKitPersister> persisters) {
        this.persisters = persisters.stream().sorted(comparingInt(UpdateKitPersister::order)).toList();
        checkDuplicateOrders(persisters);
    }

    private void checkDuplicateOrders(List<UpdateKitPersister> persisters) {
        Set<Integer> orderSet = persisters.stream().map(UpdateKitPersister::order).collect(Collectors.toSet());
        if (orderSet.size() != persisters.size())
            throw new IllegalStateException();
    }

    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit, UUID currentUserId) {
        UpdateKitPersisterContext ctx = new UpdateKitPersisterContext();
        boolean isMajorUpdate = false;
        for (UpdateKitPersister p : persisters) {
            UpdateKitPersisterResult result = p.persist(ctx, savedKit, dslKit, currentUserId);
            isMajorUpdate = isMajorUpdate || result.isMajorUpdate();
        }
        return new UpdateKitPersisterResult(isMajorUpdate);
    }
}
