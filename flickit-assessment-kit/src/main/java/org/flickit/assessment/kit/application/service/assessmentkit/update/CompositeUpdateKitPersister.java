package org.flickit.assessment.kit.application.service.assessmentkit.update;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

@Service
public class CompositeUpdateKitPersister implements UpdateKitPersister {

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

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        boolean shouldInvalidateCalcResult = false;
        for (UpdateKitPersister p : persisters) {
            UpdateKitPersisterResult result = p.persist(savedKit, dslKit);
            savedKit = result.updatedKit();
            shouldInvalidateCalcResult = shouldInvalidateCalcResult || result.shouldInvalidateCalcResult();
        }
        return new UpdateKitPersisterResult(savedKit, shouldInvalidateCalcResult);
    }

    @Override
    public int order() {
        return 0;
    }
}
