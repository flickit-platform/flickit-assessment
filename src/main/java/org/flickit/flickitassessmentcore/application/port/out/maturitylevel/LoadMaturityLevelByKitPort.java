package org.flickit.flickitassessmentcore.application.port.out.maturitylevel;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;

import java.util.List;

public interface LoadMaturityLevelByKitPort {

    Result loadMaturityLevelByKitId(Long kitId);

    record Result(List<MaturityLevel> maturityLevels) {}
}
