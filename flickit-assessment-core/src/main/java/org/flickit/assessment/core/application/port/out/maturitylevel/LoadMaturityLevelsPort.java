package org.flickit.assessment.core.application.port.out.maturitylevel;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;
import java.util.UUID;

public interface LoadMaturityLevelsPort {

    MaturityLevel load(long id, UUID assessmentId);

    List<MaturityLevel> loadByKitVersionId(Long kitVersionId);
}
