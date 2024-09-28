package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

public interface LoadMaturityLevelPort {

    MaturityLevel loadByIdAndKitVersionId(Long id, Long kitVersionId);
}
