package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentresult.InvalidateAssessmentResultByKitPort;
import org.flickit.assessment.kit.application.service.DslTranslator;
import org.flickit.assessment.kit.application.service.assessmentkit.update.CompositeUpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.CompositeUpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;
import org.flickit.assessment.kit.common.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitByDslService implements UpdateKitByDslUseCase {

    private final LoadAssessmentKitInfoPort loadAssessmentKitInfoPort;
    private final InvalidateAssessmentResultByKitPort invalidateAssessmentResultByKitPort;
    private final CompositeUpdateKitValidator validator;
    private final CompositeUpdateKitPersister persister;

    @Override
    public void update(Param param) {
        AssessmentKitDslModel dslKit = DslTranslator.parseJson(param.getDslContent());
        AssessmentKit savedKit = loadAssessmentKitInfoPort.load(param.getKitId());

        validateChanges(savedKit, dslKit);

        UpdateKitPersisterResult persistResult = persister.persist(savedKit, dslKit);
        if (persistResult.shouldInvalidateCalcResult())
            invalidateAssessmentResultByKitPort.invalidateByKitId(savedKit.getId());
    }

    private void validateChanges(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification validation = validator.validate(savedKit, dslKit);
        if (validation.hasErrors())
            throw new ValidationException(validation);
    }
}
