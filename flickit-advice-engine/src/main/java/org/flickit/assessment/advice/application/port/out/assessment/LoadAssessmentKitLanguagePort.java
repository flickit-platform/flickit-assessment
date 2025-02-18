package org.flickit.assessment.advice.application.port.out.assessment;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.UUID;

public interface LoadAssessmentKitLanguagePort {

    KitLanguage loadKitLanguage(UUID assessmentId);
}
