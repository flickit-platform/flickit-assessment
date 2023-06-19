package org.flickit.assessment.core.application.port.out.maturitylevel;

import org.flickit.assessment.core.domain.MaturityLevel;

import java.util.Set;

public interface LoadMaturityLevelByKitPort {

    Set<MaturityLevel> loadMaturityLevelByKitId(Long kitId);
}
