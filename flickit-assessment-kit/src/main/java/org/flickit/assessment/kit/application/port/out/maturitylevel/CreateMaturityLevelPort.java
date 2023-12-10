package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

public interface CreateMaturityLevelPort {

    Long persist(MaturityLevel level, Long kitId);
}
