package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.Level;

import java.util.List;

public interface LoadAssessmentKitMaturityLevelModelsByKitPort {

    List<Level> load(Long assessmentKitId);
}
