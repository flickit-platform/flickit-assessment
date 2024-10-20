package org.flickit.assessment.core.application.port.out.maturitylevel;

import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.util.List;

public interface LoadMaturityLevelsPort {

    List<MaturityLevel> loadByKitVersionId(Long kitVersionId);
}
