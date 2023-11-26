package org.flickit.assessment.kit.application.service.assessmentkit.update;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompositeUpdateKitPersister implements UpdateKitPersister {

    private final List<UpdateKitPersister> persisters;

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        boolean shouldInvalidateCalcResult = false;
        for (UpdateKitPersister p : persisters) {
            UpdateKitPersisterResult result = p.persist(savedKit, dslKit);
            shouldInvalidateCalcResult = shouldInvalidateCalcResult || result.shouldInvalidateCalcResult();
        }
        return new UpdateKitPersisterResult(shouldInvalidateCalcResult);
    }
}
