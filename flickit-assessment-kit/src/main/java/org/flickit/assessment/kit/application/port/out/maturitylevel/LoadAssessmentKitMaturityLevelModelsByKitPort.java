package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;

public interface LoadAssessmentKitMaturityLevelModelsByKitPort {

    List<MaturityLevel> loadByKitId(Long assessmentKitId);
}
