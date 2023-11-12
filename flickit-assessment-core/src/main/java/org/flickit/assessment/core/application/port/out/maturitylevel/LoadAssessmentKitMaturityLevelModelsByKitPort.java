package org.flickit.assessment.core.application.port.out.maturitylevel;

import org.flickit.assessment.kit.domain.Level;

import java.util.List;

public interface LoadAssessmentKitMaturityLevelModelsByKitPort {

    List<Level> load(Long assessmentKitId);
}
