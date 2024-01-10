package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.UUID;

public interface CreateMaturityLevelPort {

    Long persist(MaturityLevel level, Long kitId, UUID currentUserId);
}
