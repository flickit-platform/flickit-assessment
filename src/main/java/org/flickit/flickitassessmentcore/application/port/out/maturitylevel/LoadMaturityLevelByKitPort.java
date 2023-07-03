package org.flickit.flickitassessmentcore.application.port.out.maturitylevel;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.Set;

public interface LoadMaturityLevelByKitPort {

    Result loadMaturityLevelByKitId(Param param);

    record Param(Long kitId) {}

    record Result(Set<MaturityLevel> maturityLevels) {}
}
