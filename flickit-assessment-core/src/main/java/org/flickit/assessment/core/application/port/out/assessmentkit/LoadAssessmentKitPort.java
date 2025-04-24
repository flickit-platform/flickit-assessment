package org.flickit.assessment.core.application.port.out.assessmentkit;

import jakarta.annotation.Nullable;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.application.domain.AssessmentKit;

import java.util.Optional;

public interface LoadAssessmentKitPort {

    Optional<AssessmentKit> loadAssessmentKit(long kitId, @Nullable KitLanguage language);
}
