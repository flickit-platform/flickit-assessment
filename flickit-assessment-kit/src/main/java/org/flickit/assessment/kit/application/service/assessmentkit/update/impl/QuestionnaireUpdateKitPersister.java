package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitPersister implements UpdateKitPersister {

    @Override
    public void persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {

    }
}
