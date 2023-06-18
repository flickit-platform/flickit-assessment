package org.flickit.flickitassessmentcore.application.port.out.maturitylevel;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.Set;

public interface LoadMaturityLevelByKitPort {

    Set<MaturityLevel> loadMaturityLevelByKitId(Long kitId);
}
