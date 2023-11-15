package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

public interface CreateMaturityLevelPort {

    void persist(MaturityLevel level, Long kitId);
}
