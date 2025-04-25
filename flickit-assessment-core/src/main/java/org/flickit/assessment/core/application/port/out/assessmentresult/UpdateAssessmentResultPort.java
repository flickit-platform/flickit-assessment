package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.UUID;

public interface UpdateAssessmentResultPort {

    void updateKitVersionId(UUID assessmentResultId, Long kitVersionId);

    void updateLanguage(KitLanguage kitLanguage);
}
