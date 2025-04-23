package org.flickit.assessment.core.application.port.out.maturitylevel;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;
import java.util.UUID;

public interface LoadMaturityLevelsPort {

    List<MaturityLevel> loadByKitVersionId(Long kitVersionId);

    List<MaturityLevel> loadByAssessmentId(UUID assessmentId);
}
