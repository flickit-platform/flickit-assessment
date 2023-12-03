package org.flickit.assessment.kit.application.service.assessmentkit.update;

import org.flickit.assessment.kit.application.domain.AssessmentKit;

public record UpdateKitPersisterResult(AssessmentKit updatedKit, boolean shouldInvalidateCalcResult) {
}
