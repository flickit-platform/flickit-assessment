package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

public interface LoadMaturityLevelByCodePort {

    MaturityLevel loadByCode(String code, Long kitId);
}
